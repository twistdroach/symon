package com.loomcom.symon.ui;

import static java.lang.System.arraycopy;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.loomcom.symon.devices.Crtc;
import com.loomcom.symon.exceptions.MemoryAccessException;

public class CrtcVideoWindow extends VideoWindow {

	private static final long serialVersionUID = -9156949775611287305L;
	private static final Logger logger = Logger.getLogger(CrtcVideoWindow.class.getName());
    private int cursorBlinkRate;
    private boolean hideCursor;
    private Crtc crtc;
    private int[] charRom;
    private static final int CHAR_WIDTH = 8;
    private static final int CHAR_HEIGHT = 8;
    private int scanLinesPerRow;
    private int horizontalDisplayed;
    private int verticalDisplayed;
    
	public CrtcVideoWindow(Crtc crtc, int scaleX, int scaleY) throws IOException {
        // Capture some state from the CRTC that will define the
        // window size. When these values change, the window will
        // need to re-pack and redraw.
		super(crtc.getHorizontalDisplayed(), crtc.getVerticalDisplayed());
        crtc.registerListener(this);

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.crtc = crtc;
        this.charRom = loadCharRom("/ascii.rom");
        this.cursorBlinkRate = crtc.getCursorBlinkRate();
        this.scanLinesPerRow = crtc.getScanLinesPerRow();

        if (cursorBlinkRate > 0) {
            this.cursorBlinker = scheduler.scheduleAtFixedRate(new CursorBlinker(),
                                                               cursorBlinkRate,
                                                               cursorBlinkRate,
                                                               TimeUnit.MILLISECONDS);
        }
	}
	
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> cursorBlinker;


    /**
     * Runnable task that blinks the cursor.
     */
    private class CursorBlinker implements Runnable {
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (cursorBlinkRate > 0) {
                        hideCursor = !hideCursor;
                        CrtcVideoWindow.this.repaint();
                    }
                }
            });
        }
    }
    
    JPanel getPanel(int scaleX, int scaleY) {
        int rasterWidth = CHAR_WIDTH * horizontalDisplayed;
        int rasterHeight = scanLinesPerRow * verticalDisplayed;
        return new VideoPanel(this, new Dimension(rasterWidth * scaleX, rasterHeight * scaleY), scaleX, scaleY);
    }

    public Image getImage() {
        int rasterWidth = CHAR_WIDTH * horizontalDisplayed;
        int rasterHeight = scanLinesPerRow * verticalDisplayed;
        BufferedImage image = new BufferedImage(rasterWidth, rasterHeight, BufferedImage.TYPE_BYTE_BINARY);
        try {
            for (int i = 0; i < crtc.getPageSize(); i++) {
                int address = crtc.getStartAddress() + i;
                int originX = (i % horizontalDisplayed) * CHAR_WIDTH;
                int originY = (i / horizontalDisplayed) * scanLinesPerRow;
                image.getRaster().setPixels(originX, originY, CHAR_WIDTH, scanLinesPerRow, getGlyph(address));
            }
            
        } catch (MemoryAccessException ex) {
            logger.log(Level.SEVERE, "Memory Access Exception, can't paint video window! " + ex.getMessage());
        }
        return image;
    }
    
    /**
     * Returns an array of pixels (including extra scanlines, if any) corresponding to the
     * Character ROM plus cursor overlay (if any). The cursor overlay simulates an XOR
     * of the Character Rom output and the 6545 Cursor output.
     *
     * @param address The address of the character being requested.
     * @return An array of integers representing the pixel data.
     */
    private int[] getGlyph(int address) throws MemoryAccessException {
        int chr = crtc.getCharAtAddress(address);
        int romOffset = (chr & 0xff) * (CHAR_HEIGHT * CHAR_WIDTH);
        int[] glyph = new int[CHAR_WIDTH * scanLinesPerRow];

        // Populate the character
        arraycopy(charRom, romOffset, glyph, 0, CHAR_WIDTH * Math.min(CHAR_HEIGHT, scanLinesPerRow));

        // Overlay the cursor
        if (!hideCursor && crtc.isCursorEnabled() && crtc.getCursorPosition() == address) {
            int cursorStart = Math.min(glyph.length, crtc.getCursorStartLine() * CHAR_WIDTH);
            int cursorStop = Math.min(glyph.length, (crtc.getCursorStopLine() + 1) * CHAR_WIDTH);

            for (int i = cursorStart; i < cursorStop; i++) {
                glyph[i] ^= 0xff;
            }
        }

        return glyph;
    }
    
    /**
     * Load a Character ROM file and convert it into an array of pixel data usable
     * by the underlying BufferedImage's Raster.
     * <p>
     * Since the BufferedImage is a TYPE_BYTE_BINARY, the data must be converted
     * into a single byte per pixel, 0 for black and 255 for white.

     * @param resource The ROM file resource to load.
     * @return An array of glyphs, each ready for insertion.
     * @throws IOException
     */
    private int[] loadCharRom(String resource) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(this.getClass().getResourceAsStream(resource));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (bis.available() > 0) {
                bos.write(bis.read());
            }
            bos.flush();
            bos.close();

            byte[] raw = bos.toByteArray();

            // Now convert the raw ROM image into a format suitable for
            // insertion directly into the BufferedImage.
            int[] converted = new int[raw.length * CHAR_WIDTH];

            int romIndex = 0;
            for (int i = 0; i < converted.length;) {
                byte charRow = raw[romIndex++];

                for (int j = 7; j >= 0; j--) {
                    converted[i++] = ((charRow & (1 << j)) == 0) ? 0 : 0xff;
                }
            }
            return converted;
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }
    
    /**
     * Called by the CRTC on state change.
     */
    public void deviceStateChanged() {

        boolean repackNeeded = false;

        // TODO: I'm not entirely happy with this pattern, and I'd like to make it a bit DRY-er.

        if (horizontalDisplayed != crtc.getHorizontalDisplayed()) {
            horizontalDisplayed = crtc.getHorizontalDisplayed();
            repackNeeded = true;
        }

        if (verticalDisplayed != crtc.getVerticalDisplayed()) {
            verticalDisplayed = crtc.getVerticalDisplayed();
            repackNeeded = true;
        }

        if (scanLinesPerRow != crtc.getScanLinesPerRow()) {
            scanLinesPerRow = crtc.getScanLinesPerRow();
            repackNeeded = true;
        }

        if (cursorBlinkRate != crtc.getCursorBlinkRate()) {
            cursorBlinkRate = crtc.getCursorBlinkRate();

            if (cursorBlinker != null) {
                cursorBlinker.cancel(true);
                cursorBlinker = null;
                hideCursor = false;
            }

            if (cursorBlinkRate > 0) {
                cursorBlinker = scheduler.scheduleAtFixedRate(new CursorBlinker(),
                                                              cursorBlinkRate,
                                                              cursorBlinkRate,
                                                              TimeUnit.MILLISECONDS);
            }
        }

        if (repackNeeded) {
            invalidate();
            pack();
        }
    }
}

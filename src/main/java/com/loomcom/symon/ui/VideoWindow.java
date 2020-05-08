/*
 * Copyright (c) 2016 Seth J. Morabito <web@loomcom.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.loomcom.symon.ui;

import com.loomcom.symon.devices.DeviceChangeListener;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * VideoWindow represents a graphics framebuffer backed by a 6545 CRTC.
 * Each time the window's VideoPanel is repainted, the video memory is
 * scanned and converted to the appropriate bitmap representation.
 * <p>
 * The graphical representation of each character is derived from a
 * character generator ROM image. For this simulation, the Commodore PET
 * character generator ROM was chosen, but any character generator ROM
 * could be used in its place.
 * <p>
 * It may be convenient to think of this as the View (in the MVC
 * pattern sense) to the Crtc's Model and Controller. Whenever the CRTC
 * updates state in a way that may require the view to update, it calls
 * the <tt>deviceStateChange</tt> callback on this Window.
 */
public abstract class VideoWindow extends JFrame implements DeviceChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2026689525070058843L;

	private static final Logger logger = Logger.getLogger(VideoWindow.class.getName());

    abstract JPanel getPanel(int scaleX, int scaleY);
    abstract Image getImage();

    public VideoWindow(int scaleX, int scaleY) throws IOException {
        createAndShowUi(scaleX, scaleY);
    }

    private void createAndShowUi(int scaleX, int scaleY) {
        setTitle("Composite Video");
        
        int borderWidth = 20;
        int borderHeight = 20;

        JPanel containerPane = new JPanel();
        containerPane.setBorder(BorderFactory.createEmptyBorder(borderHeight, borderWidth, borderHeight, borderWidth));
        containerPane.setLayout(new BorderLayout());
        containerPane.setBackground(Color.black);

        containerPane.add(getPanel(scaleX, scaleY), BorderLayout.CENTER);

        getContentPane().add(containerPane, BorderLayout.CENTER);
        setResizable(false);
        pack();
    }
}

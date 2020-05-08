package com.loomcom.symon.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import com.loomcom.symon.devices.SearleVideo;
import com.loomcom.symon.exceptions.MemoryAccessException;

public class SearleVideoWindow extends VideoWindow {

	private static final long serialVersionUID = -4025181029628937925L;
	private static final Logger logger = Logger.getLogger(SearleVideoWindow.class.getName());
	private final SearleVideo sv;

	public SearleVideoWindow(SearleVideo sv, int scaleX, int scaleY) throws IOException {
		super(scaleX, scaleY);
		this.sv = sv;
	}

	@Override
	public void deviceStateChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	JPanel getPanel(int scaleX, int scaleY) {
		return new VideoPanel(this, new Dimension(sv.graphicsWidth * scaleX, sv.graphicsHeight * scaleY), scaleX, scaleY);
	}

	@Override
	Image getImage() {
        BufferedImage image = new BufferedImage(sv.graphicsWidth, sv.graphicsHeight, BufferedImage.TYPE_BYTE_BINARY);
        image.getRaster().setPixels(0, 0, sv.graphicsWidth, sv.graphicsHeight, sv.graphicsData);
        return image;
	}

}

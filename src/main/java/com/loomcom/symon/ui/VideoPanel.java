package com.loomcom.symon.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;



/**
 * A panel representing the composite video output, with fast Graphics2D painting.
 */
public class VideoPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6576873171278598445L;

	
	private final VideoWindow device;
	private final Dimension dimensions;
    private final int scaleX, scaleY;
    private final boolean shouldScale;
	
	public VideoPanel(VideoWindow device, Dimension dimension, int scaleX, int scaleY) {
		this.device = device;
		this.dimensions = dimension;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.shouldScale = (scaleX > 1 || scaleY > 1);
	}

	@Override
    public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
        if (shouldScale) {
            g2d.scale(scaleX, scaleY);
        }
		g2d.drawImage(device.getImage(), 0, 0, null);
    }

    @Override
    public Dimension getMinimumSize() {
        return dimensions;
    }

    @Override
    public Dimension getPreferredSize() {
        return dimensions;
    }

}

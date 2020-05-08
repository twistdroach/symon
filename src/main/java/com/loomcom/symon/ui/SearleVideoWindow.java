package com.loomcom.symon.ui;

import java.awt.Image;
import java.io.IOException;

import javax.swing.JPanel;

import com.loomcom.symon.devices.SearleVideo;

public class SearleVideoWindow extends VideoWindow {

	private static final long serialVersionUID = -4025181029628937925L;

	public SearleVideoWindow(SearleVideo sv, int scaleX, int scaleY) throws IOException {
		super(scaleX, scaleY);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void deviceStateChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	JPanel getPanel(int scaleX, int scaleY) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}

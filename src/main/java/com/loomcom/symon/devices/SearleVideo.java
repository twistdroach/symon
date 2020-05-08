package com.loomcom.symon.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearleVideo extends PortConnectedDevice {
	private final static Logger logger = LoggerFactory.getLogger(SearleVideo.class.getName());
	public static final int graphicsWidth = 160;
	public static final int graphicsHeight = 100;
	public static final int graphicsPixelCount = graphicsWidth * graphicsHeight;	
	public final int graphicsData[] = new int[graphicsPixelCount];

	private static final int SetPixel = 0x05;
	private static final int ResetPixel = 0x06;
	
	private boolean waitingForX = false;
	private boolean waitingForY = false;
	private int cachedX = 0;
	private int cachedY = 0;
	private int cachedAction = 0;
	
	@Override
	public String toString() {
		return "Grant Searle's video adapter";
	}
	
	private int getIndex(int x, int y) {
		return (y * graphicsWidth) + x;
	}
	
	private void takeAction() {
		if (cachedAction ==  SetPixel) {
			graphicsData[getIndex(cachedX, cachedY)] = 1;
		} else if (cachedAction == ResetPixel) {
			graphicsData[getIndex(cachedX, cachedY)] = 0;
		}
		waitingForX = false;
		waitingForY = false;
		cachedX = 0;
		cachedY = 0;
		cachedAction = 0;
	}

	@Override
	public void writeByte(int data) {
		// TODO Auto-generated method stub
		if (waitingForY) {
			waitingForY=false;
			cachedY = data;
			takeAction();
			return;
		}
		if (waitingForX) {
			waitingForX = false;
			waitingForY = true;
			cachedX = data;
			return;
		}
		
		switch (data) {
			case SetPixel:
				waitingForX = true;
				cachedAction = data;
				logger.info("Going to set a pixel");
				break;
			case ResetPixel:
				waitingForX = true;
				cachedAction = data;
				logger.info("Going to reset a pixel");
				break;
			default:
				logger.info("Written: " + data);
				break;
		}
	}

	@Override
	public int readByte() {
		// TODO Auto-generated method stub
		logger.info("Read byte");
		return 0;
	}

}

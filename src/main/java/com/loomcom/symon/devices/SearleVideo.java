package com.loomcom.symon.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearleVideo extends PortConnectedDevice {
	private final static Logger logger = LoggerFactory.getLogger(SearleVideo.class.getName());

	@Override
	public String toString() {
		return "Grant Searle's video adapter";
	}

	@Override
	public void writeByte(int data) {
		// TODO Auto-generated method stub
		logger.info("Written: " + data);
	}

	@Override
	public int readByte() {
		// TODO Auto-generated method stub
		logger.info("Read byte");
		return 0;
	}

}

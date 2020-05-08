package com.loomcom.symon.devices;

public abstract class PortConnectedDevice {
	abstract public void writeByte(int data);
	abstract public int readByte();

}

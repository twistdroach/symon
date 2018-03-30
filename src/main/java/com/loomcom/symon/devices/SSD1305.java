package com.loomcom.symon.devices;

import com.loomcom.symon.exceptions.MemoryAccessException;
import com.loomcom.symon.exceptions.MemoryRangeException;

public class SSD1305 extends Device {
	public static int size = 2;

	public SSD1305(int startAddress) throws MemoryRangeException {
		super(startAddress, startAddress + size, "SSD1305");
	}

	@Override
	public void write(int address, int data) throws MemoryAccessException {
		System.out.println("Write to " + address + " data " + data);

	}

	@Override
	public int read(int address, boolean cpuAccess)
			throws MemoryAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.loomcom.symon.devices;

import java.util.Random;

import com.loomcom.symon.exceptions.MemoryRangeException;

public class RandomFillMemory extends Memory {
    public RandomFillMemory(int startAddress, int endAddress) throws MemoryRangeException {
		super(startAddress, endAddress);
		randomFill();
	}
    
    public RandomFillMemory(int startAddress, int endAddress, boolean readOnly)
            throws MemoryRangeException {
        super(startAddress, endAddress, readOnly);
        randomFill();
    }

	public void randomFill() {
    	Random r = new Random();
    	for (int j=0; j< mem.length; j++) {
    		mem[j] = r.nextInt();
    	}
    }
    
    public void reset() {
    	randomFill();
    }
}

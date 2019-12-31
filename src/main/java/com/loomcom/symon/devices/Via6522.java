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

package com.loomcom.symon.devices;

import org.joou.UByte;

import com.loomcom.symon.StepListener;
import com.loomcom.symon.exceptions.MemoryAccessException;
import com.loomcom.symon.exceptions.MemoryRangeException;

/**
 * Very basic implementation of a MOS 6522 VIA.
 *
 */
public class Via6522 extends Pia implements StepListener {
    public static final int VIA_SIZE = 16;
    private final UByte[] registers = new UByte[VIA_SIZE];
    

    private static enum Register {
        ORB, ORA, DDRB, DDRA, T1C_L, T1C_H, T1L_L, T1L_H,
        T2C_L, T2C_H, SR, ACR, PCR, IFR, IER, ORA_H
    }
    private static final Register[] addressToRegister = Register.values();

    public Via6522(int address) throws MemoryRangeException {
        super(address, address + VIA_SIZE - 1, "MOS 6522 VIA");
        for( int i = 0; i < registers.length; i++) {
        	registers[i] = UByte.MIN;
        }
    }

    @Override
    public void write(int address, int data) throws MemoryAccessException {

        if (address >= addressToRegister.length) {
            throw new MemoryAccessException("Unknown register: " + address);
        }

        Register r = addressToRegister[address];

        switch (r) {
            case ORA:
            case ORB:
            case DDRA:
            case DDRB:
            case T1C_L:
            case T1C_H:
            case T1L_L:
            case T1L_H:
            case T2C_L:
            case T2C_H:
            case SR:
            case ACR:
            case PCR:
            case IFR:
            case IER:
            case ORA_H:
            default:
        }
        
        //TODO: may not want to do this...
        registers[r.ordinal()] = UByte.valueOf(data);
    }

    @Override
    public int read(int address, boolean cpuAccess) throws MemoryAccessException {
    	
        if (address >= addressToRegister.length) {
            throw new MemoryAccessException("Unknown register: " + address);
        }

        Register r = addressToRegister[address];

        switch (r) {
            case ORA:
            case ORB:
            case DDRA:
            case DDRB:
            case T1C_L:
            case T1C_H:
            case T1L_L:
            case T1L_H:
            case T2C_L:
            case T2C_H:
            case SR:
            case ACR:
            case PCR:
            case IFR:
            case IER:
            case ORA_H:
            default:
        }

        return registers[r.ordinal()].intValue();
    }

	@Override
	public void step() {
		// TODO Auto-generated method stub
		
	}
}

package com.loomcom.symon.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loomcom.symon.Cpu;
import com.loomcom.symon.exceptions.MemoryAccessException;
import com.loomcom.symon.exceptions.MemoryRangeException;
import com.loomcom.symon.util.Utils;

public class SSD1305 extends Device {
	private final static Logger logger = LoggerFactory.getLogger(SSD1305.class.getName());
	public static final int size = 2;
	private static final int COMMAND_ADDRESS = 0;
	private static final int DATA_ADDRESS = 1;
	
	public enum Command{
		SSD1306_SETLOWCOLUMN(0x00),
		SSD1305_SETHIGHCOLUMN(0x10),
		SSD1305_MEMORYMODE(0x20),
		SSD1305_SETCOLADDR(0x21),
		SSD1305_SETPAGEADDR(0x22),
		SSD1305_DISABLESCROLL(0x2E),
		SSD1305_SETSTARTLINE(0x40),

		SSD1305_SETCONTRAST(0x81),
		SSD1305_SETBRIGHTNESS(0x82),

		SSD1305_SETLUT(0x91),

		SSD1305_SEGREMAP(0xA0),
		SSD1305_SEGREMAP1(0xA1),
		SSD1305_DISPLAYALLON_RESUME(0xA4),
		SSD1305_DISPLAYALLON(0xA5),
		SSD1305_NORMALDISPLAY(0xA6),
		SSD1305_INVERTDISPLAY(0xA7),
		SSD1305_SETMULTIPLEX(0xA8),
		SSD1305_DISPLAYDIM(0xAC),
		SSD1305_MASTERCONFIG(0xAD),
		SSD1305_DISPLAYOFF(0xAE),
		SSD1305_DISPLAYON(0xAF),

		SSD1305_SETPAGESTART(0xB0),

		SSD1305_COMSCANINC(0xC0),
		SSD1305_COMSCANDEC(0xC8),
		SSD1305_SETDISPLAYOFFSET(0xD3),
		SSD1305_SETDISPLAYCLOCKDIV(0xD5),
		SSD1305_SETAREACOLOR(0xD8),
		SSD1305_SETPRECHARGE(0xD9),
		SSD1305_SETCOMPINS(0xDA),
		SSD1305_SETVCOMLEVEL(0xDB);

	    private int value;
	    
	    public static Command findByValue(final int value){
	        if ((value & 0xFF) <= 0x0F) {
	        	return SSD1306_SETLOWCOLUMN;
	        } else if ((value & 0xFF) > 0x0F && (value & 0xFF) <= 0x1F) {
	        	return SSD1305_SETHIGHCOLUMN;
	        } else if ((value & 0xFF) > 0x3F && (value & 0xFF) <= 0x7F) {
	        	return SSD1305_SETSTARTLINE;
	        }
		    // TODO probably should replace with a map implementation
	    	for(Command c : values()){
	            if(c.value == value){
	                return c;
	            }
	        }
	        return null;
	    }
	    
	    Command(final int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    @Override
	    public String toString() {
	        return this.name();
	    }
	}

	public SSD1305(int startAddress) throws MemoryRangeException {
		super(startAddress, startAddress + size, "SSD1305");
	}

	@Override
	public void write(int address, int data) throws MemoryAccessException {
		if (address == COMMAND_ADDRESS) {
			Command c = Command.findByValue(data);
			if (c == null) {
				logger.warn("Unknown command: " + Utils.byteToHex(data));
			} else {
				//TODO implement command
				System.out.println("Command: " + c.name() + " " + Utils.byteToHex(data));
			}
		} else if (address == DATA_ADDRESS) {
			System.out.println("Data write to " + Utils.wordToHex(address) + " data " + Utils.byteToHex(data));
		} else {
			throw new MemoryAccessException("Write to bad location " + Utils.wordToHex(address));
		}
	}

	@Override
	public int read(int address, boolean cpuAccess)
			throws MemoryAccessException {
		System.out.println("Read from " + Utils.wordToHex(address));
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}

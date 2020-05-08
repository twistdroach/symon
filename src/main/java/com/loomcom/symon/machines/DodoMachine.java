/*
 * Copyright (c) 2016 Seth J. Morabito <web@loomcom.com>
 *                    Maik Merten <maikmerten@googlemail.com>
 *                    Zachary D. Rowitsch <rowitsch@yahoo.com>
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

package com.loomcom.symon.machines;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loomcom.symon.Bus;
import com.loomcom.symon.Cpu;
import com.loomcom.symon.InstructionTable.CpuBehavior;
import com.loomcom.symon.devices.Acia;
import com.loomcom.symon.devices.Acia6551;
import com.loomcom.symon.devices.Crtc;
import com.loomcom.symon.devices.Memory;
import com.loomcom.symon.devices.Pia;
import com.loomcom.symon.devices.RandomFillMemory;
import com.loomcom.symon.devices.SSD1305;
import com.loomcom.symon.devices.Via6522;
import com.loomcom.symon.exceptions.MemoryRangeException;

public class DodoMachine extends Machine {
	private final static Logger logger = LoggerFactory.getLogger(DodoMachine.class.getName());

    private static final int BUS_BOTTOM = 0x0000;
    private static final int BUS_TOP    = 0xffff;

    //TODO Is this size correct?
    private static final int MEMORY_BASE = 0x0000;
    private static final int MEMORY_SIZE = 0x7F00;
    
    private static final int VIA_BASE = 0x7F00;
    
    private static final int ACIA_BASE = 0x7F10;
    
    // Display at $7F20
    private static final int DISP_BASE = 0x7F20;
    
    private static final int ROM_BASE = 0x8000;
    private static final int ROM_SIZE = 0x8000;
    
    private final Bus bus;
    private final RandomFillMemory ram;
    private final Cpu cpu;
    private final Acia   acia;
    private final Pia    via;
    private final SSD1305 ssd1305;
    private       Memory rom;

    public DodoMachine() throws Exception {
        this.bus = new Bus(BUS_BOTTOM, BUS_TOP);
        this.cpu = new Cpu(CpuBehavior.CMOS_6502);
        this.ram = new RandomFillMemory(MEMORY_BASE, MEMORY_BASE + MEMORY_SIZE - 1, false);
        this.via = new Via6522(VIA_BASE);
        this.acia = new Acia6551(ACIA_BASE);
        this.ssd1305 = new SSD1305(DISP_BASE);
        
        bus.addCpu(cpu);
        bus.addDevice(ram);
        bus.addDevice(via);
        bus.addDevice(acia);
        bus.addDevice(ssd1305);
        
        // TODO: Make this configurable, of course.
        File romImage = new File("rom.bin");
        if (romImage.canRead()) {
            logger.info("Loading ROM image from file {}", romImage);
            this.rom = Memory.makeROM(ROM_BASE, ROM_BASE + ROM_SIZE - 1, romImage);
        } else {
            logger.info("Default ROM file {} not found, loading empty R/W memory image.", romImage);
            this.rom = Memory.makeRAM(ROM_BASE, ROM_BASE + ROM_SIZE - 1);
        }

        bus.addDevice(rom);
    }

    @Override
    public Bus getBus() {
        return bus;
    }

    @Override
    public Cpu getCpu() {
        return cpu;
    }

    @Override
    public List<Memory> getRam() {
        return Arrays.asList(new Memory[] { ram });
    }

    @Override
    public Acia getAcia() {
        return acia;
    }

    @Override
    public Pia getPia() {
        return via;
    }

    @Override
    public Crtc getCrtc() {
        return null;
    }

    @Override
    public Memory getRom() {
        return rom;
    }

    @Override
    public void setRom(Memory rom) throws MemoryRangeException {
        if(this.rom != null) {
            bus.removeDevice(this.rom);
        }
        this.rom = rom;
        bus.addDevice(this.rom);
    }

    @Override
    public int getRomBase() {
        return ROM_BASE;
    }

    @Override
    public int getRomSize() {
        return ROM_SIZE;
    }

    @Override
    public int getMemorySize() {
        return MEMORY_SIZE;
    }

    @Override
    public String getName() {
        return "CNP-1";
    }
}

package com.loomcom.symon;

import com.loomcom.symon.devices.Memory;

import junit.framework.TestCase;

/**
 * Test for Zero Page Indirect addressing mode, found on some instructions
 * in the 65C02 and 65816
 */
public class Cpu65C02ZeroPageXModeTest extends TestCase {
    protected Cpu    cpu;
    protected Bus    bus;
    protected Memory mem;

    private void makeCmosCpu() throws Exception {
        makeCpu(InstructionTable.CpuBehavior.CMOS_6502);
    }

    private void makeNmosCpu() throws Exception {
        makeCpu(InstructionTable.CpuBehavior.NMOS_6502);
    }

    private void makeCpu(InstructionTable.CpuBehavior behavior) throws Exception {
        this.cpu = new Cpu(behavior);
        this.bus = new Bus(0x0000, 0xffff);
        this.mem = new Memory(0x0000, 0xffff);
        bus.addCpu(cpu);
        bus.addDevice(mem);

        // Load the reset vector.
        bus.write(0xfffc, Bus.DEFAULT_LOAD_ADDRESS & 0x00ff);
        bus.write(0xfffd, (Bus.DEFAULT_LOAD_ADDRESS & 0xff00) >>> 8);

        cpu.reset();
    }

    public void test_STZ() throws Exception {
        makeCmosCpu();
        bus.write(0x0002,0xff);

        bus.loadProgram(0x74,0x01);          // STZ Zero Page,X $01

        // Test STZ Zero Page,X ($01+1)
        assertEquals(0xff,bus.read(0x02, true));
        cpu.setXRegister(0x01);
        cpu.step();
        assertEquals(0x00,bus.read(0x02, true));
    }

    public void test_STZRequiresCmosCpu() throws Exception {
        makeNmosCpu();
        bus.write(0x0002,0xff);

        bus.loadProgram(0x74,0x01);          // STZ Zero Page,X $01

        // Test STZ Zero Page,X ($01+1)
        assertEquals(0xff,bus.read(0x02, true));
        cpu.setXRegister(0x01);
        cpu.step();
        assertEquals(0xff,bus.read(0x02, true));

    }

}

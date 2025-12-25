package program;

public class CPU {

    // 8 bit Accumulators
    public int regA;
    public int regB;
    
    // 8 bit Direct Page Register (DP)
    public int regDP;

    // 8 bit Condition Code Register (CC)
    // Flags: E F H I N Z V C (bits 7-0)
    public int regCC;

    // 16 bit Index Registers
    public int regX; 
    public int regY;

    // 16 bit Stack Pointers (S, U)
    public int regS; // system stack
    public int regU; // user stack

    // 16 bit Program Counter (PC)
    public int regPC;

    // Memory reference
    private Memory memory;
    
    // Halted flag
    public boolean halted;
    
    public CPU(Memory memory) {
        this.memory = memory;
        reset();
    }

    final public void reset() {
        regA = 0x00; 
        regB = 0x00; 
        regDP = 0x00;
        
        // Read reset vector from ROM (0xFFFE-0xFFFF)
        int highByte = memory.read(0xFFFE);
        int lowByte = memory.read(0xFFFF);
        regPC = (highByte << 8) | lowByte;

        // Initial CC, 'I' (interrupt) bit is set by default
        regCC = 0x10;

        regX = 0x0000; 
        regY = 0x0000;
        regS = 0x0000; 
        regU = 0x0000;
        
        halted = false;
    }
    
    // Get 16-bit D register (A:B combined)
    public int getRegD() {
        return (regA << 8) | regB;
    }
    
    // Set 16-bit D register
    public void setRegD(int value) {
        regA = (value >> 8) & 0xFF;
        regB = value & 0xFF;
    }
    
    // Flag bit positions
    private static final int FLAG_C = 0x01; // Carry
    private static final int FLAG_V = 0x02; // Overflow
    private static final int FLAG_Z = 0x04; // Zero
    private static final int FLAG_N = 0x08; // Negative
    private static final int FLAG_I = 0x10; // IRQ Mask
    private static final int FLAG_H = 0x20; // Half Carry
    private static final int FLAG_F = 0x40; // FIRQ Mask
    private static final int FLAG_E = 0x80; // Entire state saved
    
    // Set/Clear flags
    private void setFlag(int flag) {
        regCC |= flag;
    }
    
    private void clearFlag(int flag) {
        regCC &= ~flag;
    }
    
    private boolean isFlagSet(int flag) {
        return (regCC & flag) != 0;
    }
    
    // Update N and Z flags based on 8-bit value
    private void updateNZ(int value) {
        value = value & 0xFF;
        if (value == 0) {
            setFlag(FLAG_Z);
        } else {
            clearFlag(FLAG_Z);
        }
        
        if ((value & 0x80) != 0) {
            setFlag(FLAG_N);
        } else {
            clearFlag(FLAG_N);
        }
    }
    
    // Update N and Z flags based on 16-bit value
    private void updateNZ16(int value) {
        value = value & 0xFFFF;
        if (value == 0) {
            setFlag(FLAG_Z);
        } else {
            clearFlag(FLAG_Z);
        }
        
        if ((value & 0x8000) != 0) {
            setFlag(FLAG_N);
        } else {
            clearFlag(FLAG_N);
        }
    }
    
    // Fetch next byte from memory and increment PC
    private int fetchByte() {
        int value = memory.read(regPC);
        regPC = (regPC + 1) & 0xFFFF;
        return value;
    }
    
    // Fetch next word (16-bit) from memory
    private int fetchWord() {
        int high = fetchByte();
        int low = fetchByte();
        return (high << 8) | low;
    }
    
    // Execute one instruction and return its description
    public String step() {
        if (halted) {
            return "CPU HALTED";
        }
        
        int opcode = fetchByte();
        return executeInstruction(opcode);
    }
    
    // Execute instruction based on opcode
    private String executeInstruction(int opcode) {
        switch (opcode) {
            // LDA - Load Accumulator A
            case 0x86: { // Immediate
                int value = fetchByte();
                regA = value & 0xFF;
                updateNZ(regA);
                return String.format("LDA #$%02X", value);
            }
            case 0x96: { // Direct
                int addr = fetchByte();
                regA = memory.read(addr) & 0xFF;
                updateNZ(regA);
                return String.format("LDA $%02X", addr);
            }
            case 0xB6: { // Extended
                int addr = fetchWord();
                regA = memory.read(addr) & 0xFF;
                updateNZ(regA);
                return String.format("LDA $%04X", addr);
            }
            
            // LDB - Load Accumulator B
            case 0xC6: { // Immediate
                int value = fetchByte();
                regB = value & 0xFF;
                updateNZ(regB);
                return String.format("LDB #$%02X", value);
            }
            case 0xD6: { // Direct
                int addr = fetchByte();
                regB = memory.read(addr) & 0xFF;
                updateNZ(regB);
                return String.format("LDB $%02X", addr);
            }
            case 0xF6: { // Extended
                int addr = fetchWord();
                regB = memory.read(addr) & 0xFF;
                updateNZ(regB);
                return String.format("LDB $%04X", addr);
            }
            
            // LDX - Load Index Register X
            case 0x8E: { // Immediate
                regX = fetchWord();
                updateNZ16(regX);
                return String.format("LDX #$%04X", regX);
            }
            case 0x9E: { // Direct
                int addr = fetchByte();
                int high = memory.read(addr);
                int low = memory.read(addr + 1);
                regX = ((high << 8) | low) & 0xFFFF;
                updateNZ16(regX);
                return String.format("LDX $%02X", addr);
            }
            case 0xBE: { // Extended
                int addr = fetchWord();
                int high = memory.read(addr);
                int low = memory.read(addr + 1);
                regX = ((high << 8) | low) & 0xFFFF;
                updateNZ16(regX);
                return String.format("LDX $%04X", addr);
            }
            
            // STA - Store Accumulator A
            case 0x97: { // Direct
                int addr = fetchByte();
                memory.write(addr, regA);
                return String.format("STA $%02X", addr);
            }
            case 0xB7: { // Extended
                int addr = fetchWord();
                memory.write(addr, regA);
                return String.format("STA $%04X", addr);
            }
            
            // STB - Store Accumulator B
            case 0xD7: { // Direct
                int addr = fetchByte();
                memory.write(addr, regB);
                return String.format("STB $%02X", addr);
            }
            case 0xF7: { // Extended
                int addr = fetchWord();
                memory.write(addr, regB);
                return String.format("STB $%04X", addr);
            }
            
            // STX - Store Index Register X
            case 0x9F: { // Direct
                int addr = fetchByte();
                memory.write(addr, (regX >> 8) & 0xFF);
                memory.write(addr + 1, regX & 0xFF);
                return String.format("STX $%02X", addr);
            }
            case 0xBF: { // Extended
                int addr = fetchWord();
                memory.write(addr, (regX >> 8) & 0xFF);
                memory.write(addr + 1, regX & 0xFF);
                return String.format("STX $%04X", addr);
            }
            
            // ADDA - Add to A
            case 0x8B: { // Immediate
                int value = fetchByte();
                int result = regA + value;
                updateNZ(result);
                if (result > 0xFF) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regA = result & 0xFF;
                return String.format("ADDA #$%02X", value);
            }
            case 0x9B: { // Direct
                int addr = fetchByte();
                int value = memory.read(addr);
                int result = regA + value;
                updateNZ(result);
                if (result > 0xFF) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regA = result & 0xFF;
                return String.format("ADDA $%02X", addr);
            }
            case 0xBB: { // Extended
                int addr = fetchWord();
                int value = memory.read(addr);
                int result = regA + value;
                updateNZ(result);
                if (result > 0xFF) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regA = result & 0xFF;
                return String.format("ADDA $%04X", addr);
            }
            
            // ADDB - Add to B
            case 0xCB: { // Immediate
                int value = fetchByte();
                int result = regB + value;
                updateNZ(result);
                if (result > 0xFF) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regB = result & 0xFF;
                return String.format("ADDB #$%02X", value);
            }
            case 0xDB: { // Direct
                int addr = fetchByte();
                int value = memory.read(addr);
                int result = regB + value;
                updateNZ(result);
                if (result > 0xFF) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regB = result & 0xFF;
                return String.format("ADDB $%02X", addr);
            }
            case 0xFB: { // Extended
                int addr = fetchWord();
                int value = memory.read(addr);
                int result = regB + value;
                updateNZ(result);
                if (result > 0xFF) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regB = result & 0xFF;
                return String.format("ADDB $%04X", addr);
            }
            
            // SUBA - Subtract from A
            case 0x80: { // Immediate
                int value = fetchByte();
                int result = regA - value;
                updateNZ(result);
                if (result < 0) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regA = result & 0xFF;
                return String.format("SUBA #$%02X", value);
            }
            case 0x90: { // Direct
                int addr = fetchByte();
                int value = memory.read(addr);
                int result = regA - value;
                updateNZ(result);
                if (result < 0) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regA = result & 0xFF;
                return String.format("SUBA $%02X", addr);
            }
            case 0xB0: { // Extended
                int addr = fetchWord();
                int value = memory.read(addr);
                int result = regA - value;
                updateNZ(result);
                if (result < 0) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regA = result & 0xFF;
                return String.format("SUBA $%04X", addr);
            }
            
            // SUBB - Subtract from B
            case 0xC0: { // Immediate
                int value = fetchByte();
                int result = regB - value;
                updateNZ(result);
                if (result < 0) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regB = result & 0xFF;
                return String.format("SUBB #$%02X", value);
            }
            case 0xD0: { // Direct
                int addr = fetchByte();
                int value = memory.read(addr);
                int result = regB - value;
                updateNZ(result);
                if (result < 0) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regB = result & 0xFF;
                return String.format("SUBB $%02X", addr);
            }
            case 0xF0: { // Extended
                int addr = fetchWord();
                int value = memory.read(addr);
                int result = regB - value;
                updateNZ(result);
                if (result < 0) setFlag(FLAG_C); else clearFlag(FLAG_C);
                regB = result & 0xFF;
                return String.format("SUBB $%04X", addr);
            }
            
            // INCA - Increment A
            case 0x4C: {
                regA = (regA + 1) & 0xFF;
                updateNZ(regA);
                return "INCA";
            }
            
            // INCB - Increment B
            case 0x5C: {
                regB = (regB + 1) & 0xFF;
                updateNZ(regB);
                return "INCB";
            }
            
            // DECA - Decrement A
            case 0x4A: {
                regA = (regA - 1) & 0xFF;
                updateNZ(regA);
                return "DECA";
            }
            
            // DECB - Decrement B
            case 0x5A: {
                regB = (regB - 1) & 0xFF;
                updateNZ(regB);
                return "DECB";
            }
            
            // BRA - Branch Always
            case 0x20: {
                int offset = fetchByte();
                // Sign extend 8-bit offset
                if ((offset & 0x80) != 0) {
                    offset |= 0xFF00;
                }
                regPC = (regPC + offset) & 0xFFFF;
                return String.format("BRA $%04X", regPC);
            }
            
            // BEQ - Branch if Equal (Z=1)
            case 0x27: {
                int offset = fetchByte();
                if (isFlagSet(FLAG_Z)) {
                    if ((offset & 0x80) != 0) {
                        offset |= 0xFF00;
                    }
                    regPC = (regPC + offset) & 0xFFFF;
                    return String.format("BEQ $%04X (taken)", regPC);
                }
                return "BEQ (not taken)";
            }
            
            // BNE - Branch if Not Equal (Z=0)
            case 0x26: {
                int offset = fetchByte();
                if (!isFlagSet(FLAG_Z)) {
                    if ((offset & 0x80) != 0) {
                        offset |= 0xFF00;
                    }
                    regPC = (regPC + offset) & 0xFFFF;
                    return String.format("BNE $%04X (taken)", regPC);
                }
                return "BNE (not taken)";
            }
            
            // NOP - No Operation
            case 0x12: {
                return "NOP";
            }
            
            // END marker (halt)
            case 0x00: {
                halted = true;
                return "END - PROGRAM HALTED";
            }
            
            default:
                halted = true;
                return String.format("INVALID OPCODE: $%02X", opcode);
        }
    }
}
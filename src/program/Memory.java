package program;

public class Memory {
    private RAM ram;
    private ROM rom;
    
    public Memory() {
        ram = new RAM(32768); // 32KB RAM (0x0000 - 0x7FFF)
        rom = new ROM(32768); // 32KB ROM (0x8000 - 0xFFFF)
        initializeROM();
    }
    
    // Initialize ROM with reset vector pointing to RAM start
    private void initializeROM() {
        // Reset vector at 0xFFFE-0xFFFF points to 0x0000 (start of RAM)
        // In ROM's local addressing (0-32767), 0xFFFE is at index 0x7FFE
        rom.write(0x7FFE, 0x00); // High byte of address 0x0000
        rom.write(0x7FFF, 0x00); // Low byte of address 0x0000
    }
    
    // Read from memory (checks RAM or ROM based on address)
    public int read(int address) {
        address = address & 0xFFFF; // Ensure 16-bit address
        if (address < 0x8000) {
            return ram.read(address);
        } else {
            return rom.read(address - 0x8000); // Offset for ROM
        }
    }
    
    // Write to memory (only RAM is writable)
    public void write(int address, int value) {
        address = address & 0xFFFF;
        if (address < 0x8000) {
            ram.write(address, value);
        }
        // ROM writes are ignored
    }
    
    // Get RAM reference for UI updates
    public RAM getRAM() {
        return ram;
    }
    
    // Get ROM reference for UI updates
    public ROM getROM() {
        return rom;
    }
    
    // Reset both RAM and ROM
    public void reset() {
        ram.reset();
        rom.reset();
        initializeROM();
    }
}
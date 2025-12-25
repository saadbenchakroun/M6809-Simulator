package program;

public class RAM {
    int[] memory;
    int size;

    public RAM(int size) {
        this.size = size;
        this.memory = new int[size];
        reset();
    }

    // reset ram
    final public void reset() {
        for (int i=0; i<size; i++) 
            memory[i] = 0x00;
    }

    // read address
    public int read(int address) {
        if (address >= 0 && address < size) 
            return memory[address];
        return 0;
    }

    // write into memory
    public void write(int address, int value) {
        if (address >= 0 && address < size) 
            memory[address] = value & 0xFF; // 8 bit value
    }

    // get size of ram
    public int getSize() {
        return size;
    }
}

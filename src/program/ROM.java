package program;

public class ROM {
    
    int[] memory;
    int size;

    public ROM(int size) {
        this.size = size;
        this.memory = new int[size];
        reset();
    }

    final public void reset() {
        for (int i=0; i<size; i++) 
            memory[i] = 0xFF;
    }

    public int read(int address) {
        if (address >= 0 && address < size) 
            return memory[address];
        return 0x00;
    }

    public void write(int address, int value) {
        if (address >= 0 && address < size) 
            memory[address] = value & 0xFF;
    }

    public int getSize() {
        return size;
    }

}

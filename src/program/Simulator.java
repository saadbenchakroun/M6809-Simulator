package program;

public class Simulator {
    private CPU cpu;
    private Memory memory;
    private Assembler assembler;
    
    public Simulator() {
        memory = new Memory();
        cpu = new CPU(memory);
        assembler = new Assembler();
    }
    
    // Assemble source code
    public boolean assemble(String sourceCode) {
        return assembler.assemble(sourceCode);
    }
    
    // Load assembled program into memory
    public void loadProgram() {
        assembler.loadIntoMemory(memory);
    }
    
    // Reset CPU
    public void reset() {
        cpu.reset();
    }
    
    // Execute one instruction
    public String step() {
        return cpu.step();
    }
    
    // Run until halt
    public void run() {
        while (!cpu.halted) {
            cpu.step();
        }
    }
    
    // Getters
    public CPU getCPU() {
        return cpu;
    }
    
    public Memory getMemory() {
        return memory;
    }
    
    public String getAssemblerError() {
        return assembler.getErrorMessage();
    }
}
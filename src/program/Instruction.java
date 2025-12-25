package program;

public class Instruction {
    public String mnemonic;      // e.g., "LDA", "STA"
    public String addressMode;   // "IMMEDIATE", "DIRECT", "INHERENT", "EXTENDED"
    public int operand;          // The value or address
    public int opcode;           // The actual opcode byte
    public int size;             // Total instruction size in bytes
    
    public Instruction(String mnemonic, String addressMode, int operand) {
        this.mnemonic = mnemonic.toUpperCase();
        this.addressMode = addressMode;
        this.operand = operand;
        this.size = calculateSize();
        this.opcode = getOpcode();
    }
    
    // Calculate instruction size based on addressing mode
    private int calculateSize() {
        switch (addressMode) {
            case "INHERENT":
                return 1; // Just opcode
            case "IMMEDIATE":
                return 2; // Opcode + immediate value
            case "DIRECT":
                return 2; // Opcode + direct address (8-bit)
            case "EXTENDED":
                return 3; // Opcode + extended address (16-bit)
            default:
                return 1;
        }
    }
    
    // Get opcode for instruction (simplified set)
    private int getOpcode() {
        switch (mnemonic) {
            // Load instructions
            case "LDA":
                return addressMode.equals("IMMEDIATE") ? 0x86 : 
                       addressMode.equals("DIRECT") ? 0x96 : 0xB6;
            case "LDB":
                return addressMode.equals("IMMEDIATE") ? 0xC6 : 
                       addressMode.equals("DIRECT") ? 0xD6 : 0xF6;
            case "LDX":
                return addressMode.equals("IMMEDIATE") ? 0x8E : 
                       addressMode.equals("DIRECT") ? 0x9E : 0xBE;
            
            // Store instructions
            case "STA":
                return addressMode.equals("DIRECT") ? 0x97 : 0xB7;
            case "STB":
                return addressMode.equals("DIRECT") ? 0xD7 : 0xF7;
            case "STX":
                return addressMode.equals("DIRECT") ? 0x9F : 0xBF;
            
            // Arithmetic
            case "ADDA":
                return addressMode.equals("IMMEDIATE") ? 0x8B : 
                       addressMode.equals("DIRECT") ? 0x9B : 0xBB;
            case "ADDB":
                return addressMode.equals("IMMEDIATE") ? 0xCB : 
                       addressMode.equals("DIRECT") ? 0xDB : 0xFB;
            case "SUBA":
                return addressMode.equals("IMMEDIATE") ? 0x80 : 
                       addressMode.equals("DIRECT") ? 0x90 : 0xB0;
            case "SUBB":
                return addressMode.equals("IMMEDIATE") ? 0xC0 : 
                       addressMode.equals("DIRECT") ? 0xD0 : 0xF0;
            
            // Increment/Decrement
            case "INCA":
                return 0x4C;
            case "INCB":
                return 0x5C;
            case "DECA":
                return 0x4A;
            case "DECB":
                return 0x5A;
            
            // Branches
            case "BRA":
                return 0x20; // Branch always
            case "BEQ":
                return 0x27; // Branch if equal (Z=1)
            case "BNE":
                return 0x26; // Branch if not equal (Z=0)
            
            // Special
            case "NOP":
                return 0x12;
            case "END":
                return 0x00; // Our custom end marker
                
            default:
                return 0x00;
        }
    }
    
    @Override
    public String toString() {
        switch (addressMode) {
            case "IMMEDIATE":
                return String.format("%s #$%02X", mnemonic, operand);
            case "DIRECT":
                return String.format("%s $%02X", mnemonic, operand);
            case "EXTENDED":
                return String.format("%s $%04X", mnemonic, operand);
            case "INHERENT":
                return mnemonic;
            default:
                return mnemonic;
        }
    }
}
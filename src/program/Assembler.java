package program;

import java.util.ArrayList;
import java.util.List;

public class Assembler {
    private List<Instruction> instructions;
    private String errorMessage;
    
    public Assembler() {
        instructions = new ArrayList<>();
        errorMessage = null;
    }
    
    // Assemble the source code
    public boolean assemble(String sourceCode) {
        instructions.clear();
        errorMessage = null;
        
        String[] lines = sourceCode.split("\n");
        boolean foundEnd = false;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith(";")) {
                continue;
            }
            
            // Check for END directive
            if (line.equalsIgnoreCase("END")) {
                foundEnd = true;
                break;
            }
            
            // Parse the instruction
            try {
                Instruction instr = parseLine(line);
                if (instr != null) {
                    instructions.add(instr);
                }
            } catch (Exception e) {
                errorMessage = "Syntax Error at line " + (i + 1) + ": " + line;
                return false;
            }
        }
        
        // Check if END was found
        if (!foundEnd) {
            errorMessage = "Error: Program must end with 'END' directive";
            return false;
        }
        
        return true;
    }
    
    // Parse a single line of assembly
    private Instruction parseLine(String line) throws Exception {
        // Remove comments
        int commentIndex = line.indexOf(';');
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }
        line = line.trim();
        
        if (line.isEmpty()) {
            return null;
        }
        
        // Split into parts
        String[] parts = line.split("\\s+");
        if (parts.length == 0) {
            return null;
        }
        
        String mnemonic = parts[0].toUpperCase();
        
        // Inherent addressing (no operand)
        if (parts.length == 1) {
            if (isInherentInstruction(mnemonic)) {
                return new Instruction(mnemonic, "INHERENT", 0);
            } else {
                throw new Exception("Invalid instruction or missing operand");
            }
        }
        
        // Has operand
        String operandStr = parts[1];
        
        // Immediate addressing (#$XX or #XX)
        if (operandStr.startsWith("#")) {
            operandStr = operandStr.substring(1); // Remove #
            int value = parseValue(operandStr);
            return new Instruction(mnemonic, "IMMEDIATE", value);
        }
        
        // Direct or Extended addressing ($XX or $XXXX)
        int address = parseValue(operandStr);
        
        // Determine if direct (8-bit) or extended (16-bit)
        if (address <= 0xFF) {
            return new Instruction(mnemonic, "DIRECT", address);
        } else {
            return new Instruction(mnemonic, "EXTENDED", address);
        }
    }
    
    // Check if instruction is inherent (no operand)
    private boolean isInherentInstruction(String mnemonic) {
        switch (mnemonic) {
            case "NOP":
            case "INCA":
            case "INCB":
            case "DECA":
            case "DECB":
                return true;
            default:
                return false;
        }
    }
    
    // Parse hex or decimal value
    private int parseValue(String str) throws Exception {
        str = str.trim();
        if (str.startsWith("$")) {
            // Hexadecimal
            return Integer.parseInt(str.substring(1), 16);
        } else {
            // Decimal
            return Integer.parseInt(str);
        }
    }
    
    // Load assembled program into memory
    public void loadIntoMemory(Memory memory) {
        int address = 0x0000; // Start at beginning of RAM
        
        for (Instruction instr : instructions) {
            // Write opcode
            memory.write(address++, instr.opcode);
            
            // Write operand if needed
            if (instr.size == 2) {
                memory.write(address++, instr.operand & 0xFF);
            } else if (instr.size == 3) {
                memory.write(address++, (instr.operand >> 8) & 0xFF); // High byte
                memory.write(address++, instr.operand & 0xFF);        // Low byte
            }
        }
    }
    
    public List<Instruction> getInstructions() {
        return instructions;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
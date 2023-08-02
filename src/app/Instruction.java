package app;

import java.util.Map;
import java.util.TreeMap;

public class Instruction {
    private Integer decWord;
    private Integer address;
    private String hexWord;
    private String binWord;
    private String assembly;
    
    private boolean assigned;
    private Boolean pcIsHere;
    private Boolean isBeingEdited;
    
    private static Map<String,String> opcodeToMnemonic;
    private static Map<String,String> regToMnemonic;
    private static Map<String,String> ccToMnemonic;
    
    private static Map<String,String> opcodeToBin;
    private static Map<String,String> regToBin;
    private static Map<String,String> ccToBin;

    public Instruction(Integer address) {
        this.pcIsHere = (address == 0);
        this.isBeingEdited = (address == 0);
        this.initTranslationTables();
        this.address = address;
        this.assigned = false;
    }
    
    public Instruction(Instruction inst) {
        this(inst.address);
        this.decWord = inst.decWord;
        this.pcIsHere = inst.pcIsHere;
        this.hexWord = inst.hexWord;
        this.binWord = inst.binWord;
        this.assembly = inst.assembly;
        this.assigned = inst.assigned;
        this.isBeingEdited = inst.isBeingEdited;
    }
    
    public Instruction(Integer address, String hexWord) {
        this(address);
        this.assigned = true;
        this.hexWord = hexWord;
        this.decWord = Integer.parseInt(hexWord, 16);
        this.binWord = this.parseBinWord();
        this.assembly = this.parseAssembly();
        
    }
    
    public Instruction(Integer address, Integer decWord) {
        this(address);
        this.assigned = true;
        this.decWord = decWord;
        this.hexWord = this.parseHexWord();
        this.binWord = this.parseBinWord();
        this.assembly = this.parseAssembly();
    }
    
    public Instruction(Integer address, String inst, String reg, String cc, String mem) throws IllegalInstructionException {
        this(address);
        
        String binContent;        
        if(this.isValidInstruction(inst, reg, cc, mem)) {
            binContent = (inst == null || "".equals(inst)) ? "000" : this.opcodeToBin.get(inst);

            if(inst.equals("JC")) {
                binContent += (cc == null || "".equals(cc)) ? "0" : this.ccToBin.get(cc);
            }
            else if(inst.equals("LDR") || inst.equals("STR") || inst.equals("ADD") || inst.equals("SUB") ) {
                binContent += (reg == null || "".equals(reg)) ? "0" : this.regToBin.get(reg);
            }
            else {
                binContent += "0";
            }

            if("".equals(mem)) {
                binContent +=  "0000";
            }
            else {
                try {            
                    String binMem = Integer.toBinaryString(Integer.parseInt(mem));

                    while(binMem.length() < 4) {
                        binMem = "0" + binMem;
                    }
                    binContent += binMem;
                }
                catch(NumberFormatException nfe) {
                    binContent += "0000";
                }
            }

            this.binWord = binContent;
            this.assigned = true;
            this.decWord = Integer.parseInt(binWord, 2);
            this.hexWord = String.format("%02x", this.decWord);
            this.assembly = this.parseAssembly();
        }
        else {
            throw new IllegalInstructionException();
        }        
        
    }
    
    private boolean isValidInstruction(String inst, String reg, String cc, String mem) {
        boolean isRegInst = (inst.equals("LDR") || inst.equals("STR") || inst.equals("ADD") || inst.equals("SUB"));
        boolean isJC = inst.equals("JC");
        boolean isJMP = inst.equals("JMP");
        boolean isHLT = inst.equals("HLT");

        boolean hasReg = !reg.equals("");
        boolean hasCC = !cc.equals("");
        boolean hasMem = !mem.equals("");

        return (((isRegInst && hasReg) || (isJC && hasCC) || (isJMP)) && hasMem) || isHLT;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
        this.assembly = this.parseAssembly();
    }

    private String parseHexWord() {
        return String.format("%04x", this.decWord).toUpperCase();
    }
    
    private String parseBinWord() {
        String bin = Integer.toBinaryString(this.decWord);
        while(bin.length() < 8) {
            bin = "0" + bin;
        }
        return bin;        
    }

    private void initTranslationTables() {
        opcodeToMnemonic = new TreeMap<String,String>();
        opcodeToMnemonic.put("000", "LDR");
        opcodeToMnemonic.put("001", "STR");
        opcodeToMnemonic.put("010", "ADD");
        opcodeToMnemonic.put("011", "SUB");
        opcodeToMnemonic.put("100", "JMP");
        opcodeToMnemonic.put("101", "JC");
        opcodeToMnemonic.put("111", "HLT");
        
        regToMnemonic = new TreeMap<String,String>();
        regToMnemonic.put("0", "RA");
        regToMnemonic.put("1", "RB");
        
        ccToMnemonic = new TreeMap<String,String>();
        ccToMnemonic.put("0", "Z");
        ccToMnemonic.put("1", "N");

        opcodeToBin = new TreeMap();
        opcodeToBin.put("LDR", "000");
        opcodeToBin.put("STR", "001");
        opcodeToBin.put("ADD", "010");
        opcodeToBin.put("SUB", "011");
        opcodeToBin.put("JMP", "100");
        opcodeToBin.put("JC", "101");
        opcodeToBin.put("HLT", "111");

        regToBin = new TreeMap();
        regToBin.put("RA", "0");
        regToBin.put("RB", "1");

        ccToBin = new TreeMap();
        ccToBin.put("Z", "0");
        ccToBin.put("N", "1");
    }
    
    public String getOpcode() {
        return opcodeToMnemonic.get(this.binWord.substring(0, 3));
    }
    
    public String getReg() {
        return regToMnemonic.get(this.binWord.substring(3, 4));
    }
    
    public String getCC() {
        return ccToMnemonic.get(this.binWord.substring(3, 4));
    }

    public Integer getMemAddress() {
        return Integer.parseInt(this.binWord.substring(4,8), 2);
    }
    
    private String parseAssembly() {
        String returnable = "";
        if(this.assigned) {
            returnable = this.getOpcode() + " ";
            if(this.isCondJumpInstruction()) {
                returnable += this.getCC() + " ";
            }
            else if(this.isRegInstruction()){
                returnable += this.getReg() + " ";
            }
            if(!this.getOpcode().equals("HLT")) {
                returnable += this.getMemAddress();
            }
        }
        return returnable;
    }
    
    public boolean isJumpInstruction() {
        if(!this.isAssigned())
            return false;
        return this.getOpcode().equals("JMP") || this.getOpcode().equals("JC");
    }
    
    public boolean isCondJumpInstruction() {
        if(!this.isAssigned())
            return false;
        return this.getOpcode().equals("JC");
    }
    
    public boolean isRegInstruction() {
        if(!this.isAssigned())
            return false;
        return this.getOpcode().equals("LDR") || this.getOpcode().equals("STR") || this.getOpcode().equals("ADD") || this.getOpcode().equals("SUB");
    }    
    
    boolean isHLT() {
        if(!this.isAssigned())
            return false;
        return this.getOpcode().equals("HLT");
    }
    
    public Integer getWord() {
        return decWord;
    }

    public String getPcIsHere() {
        return (this.pcIsHere) ? "⇒" : "";
    }

    public void setPcIsHere(Boolean pcIsHere) {
        this.pcIsHere = pcIsHere;
    }

    public Integer getAddress() {
        return address;
    }

    public String getHexWord() {
        if(hexWord != null) {
            return hexWord.toUpperCase();
        }
        return hexWord;
    }

    public String getAssembly() {
        return assembly;
    }

    public String getBinWord() {
        return binWord;
    }
    
    public String toString() {
        return this.assembly;
    }  

    public String getIsBeingEdited() {
        return isBeingEdited ? "⇒" : "";
    }

    public void setIsBeingEdited(Boolean isBeingEdited) {
        this.isBeingEdited = isBeingEdited;
    }

    
}

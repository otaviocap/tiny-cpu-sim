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
    private static Map<String,String> modeToMnemonic;
    
    private static Map<String,String> opcodeToBin;
    private static Map<String,String> regToBin;
    private static Map<String,String> ccToBin;
    private static Map<String,String> modeToBin;

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
    
    public Instruction(Integer address, String inst, String reg, String cc, String mode, String mem) throws IllegalInstructionException {
        this(address);
        
        String binContent;        
        if(this.isValidInstruction(inst, reg, cc, mode, mem)) {
            binContent = (inst == null || "".equals(inst)) ? "000" : this.opcodeToBin.get(inst);
            binContent += (reg == null || "".equals(reg)) ? "00" : this.regToBin.get(reg);
            binContent += (cc == null || "".equals(cc)) ? "0" : this.ccToBin.get(cc);
            binContent += (mode == null || "".equals(mode)) ? "00" : this.modeToBin.get(mode);

            if("".equals(mem)) {
                binContent +=  "00000000";
            }
            else {
                try {            
                    String binMem = Integer.toBinaryString(Integer.parseInt(mem));

                    while(binMem.length() < 8) {
                        binMem = "0" + binMem;
                    }
                    binContent += binMem;
                }
                catch(NumberFormatException nfe) {
                    binContent += "00000000";
                }
            }

            this.binWord = binContent;
            this.assigned = true;
            this.decWord = Integer.parseInt(binWord, 2);
            this.hexWord = String.format("%04x", this.decWord);
            this.assembly = this.parseAssembly();
        }
        else {
            throw new IllegalInstructionException();
        }        
        
    }
    
    private boolean isValidInstruction(String inst, String reg, String cc, String mode, String mem) {       
        boolean isRegInst = (inst.equals("LDR") || inst.equals("STR") || inst.equals("ADD") || inst.equals("SUB"));
        boolean isJC = inst.equals("JC");
        boolean isJMP = inst.equals("JMP");
        boolean isHLT = inst.equals("HLT");

        boolean hasReg = !reg.equals("");
        boolean hasCC = !cc.equals("");
        boolean hasMode = !mode.equals("");
        boolean hasMem = !mem.equals("");

        return (((isRegInst && hasReg && hasMode) || (isJC && hasCC) || (isJMP)) && hasMem) || isHLT; 
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
        while(bin.length() < 16) {
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
        regToMnemonic.put("00", "RA");
        regToMnemonic.put("01", "RB");
        regToMnemonic.put("10", "RC");
        regToMnemonic.put("11", "RX");
        
        ccToMnemonic = new TreeMap<String,String>();
        ccToMnemonic.put("0", "Z");
        ccToMnemonic.put("1", "N");
        
        modeToMnemonic = new TreeMap();
        modeToMnemonic.put("00", "DIR");
        modeToMnemonic.put("01", "IMM");
        modeToMnemonic.put("10", "IDX");
        
        opcodeToBin = new TreeMap();
        opcodeToBin.put("LDR", "000");
        opcodeToBin.put("STR", "001");
        opcodeToBin.put("ADD", "010");
        opcodeToBin.put("SUB", "011");
        opcodeToBin.put("JMP", "100");
        opcodeToBin.put("JC", "101");
        opcodeToBin.put("HLT", "111");

        regToBin = new TreeMap();
        regToBin.put("RA", "00");
        regToBin.put("RB", "01");
        regToBin.put("RC", "10");
        regToBin.put("RX", "11");

        ccToBin = new TreeMap();
        ccToBin.put("Z", "0");
        ccToBin.put("N", "1");
        
        modeToBin = new TreeMap();
        modeToBin.put("DIR", "00");
        modeToBin.put("IMM", "01");
        modeToBin.put("IDX", "10");
    }
    
    public String getOpcode() {
        return opcodeToMnemonic.get(this.binWord.substring(0, 3));
    }
    
    public String getReg() {
        return regToMnemonic.get(this.binWord.substring(3, 5));
    }
    
    public String getCC() {
        return ccToMnemonic.get(this.binWord.substring(5, 6));
    }
    
    public String getMode() {
        return modeToMnemonic.get(this.binWord.substring(6, 8));
    }
    
    public Integer getMemAddress() {
        return Integer.parseInt(this.binWord.substring(8,16), 2);
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
                if(this.getMode().equals("DIR")) {
                    returnable += this.getMemAddress();
                }
                else if(this.getMode().equals("IMM")) {
                    returnable += "#" + this.getMemAddress();
                }
                else {
                    returnable += this.getMemAddress() + ",X";
                }
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

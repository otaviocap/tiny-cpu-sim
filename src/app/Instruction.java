package app;

import java.util.Map;
import java.util.TreeMap;

public class Instruction {
    private Integer word;
    private Boolean pcIsHere;
    private Integer address;
    private String hexWord;
    private String binWord;
    private String assembly;
    
    private boolean assigned;
    
    private Map<String,String> opcodeToMnemonic;
    private Map<String,String> regToMnemonic;
    private Map<String,String> ccToMnemonic;

    public Instruction(Integer word, Integer address) {
        this.word = word;
        this.address = address;
        this.pcIsHere = (address == 0) ? true : false;
        this.hexWord = this.parseHexWord();
        this.binWord = this.parseBinWord();
        
        this.initTranslationTables();
        this.assembly = this.parseAssembly();
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
        this.assembly = this.parseAssembly();
    }

    private String parseHexWord() {
        return String.format("%02x", this.word);
    }
    
    private String parseBinWord() {
        String bin = Integer.toBinaryString(this.word);
        while(bin.length() < 8) {
            bin = "0" + bin;
        }
        return bin;        
    }

    private void initTranslationTables() {
        this.opcodeToMnemonic = new TreeMap<String,String>();
        this.opcodeToMnemonic.put("000", "LDR");
        this.opcodeToMnemonic.put("001", "STR");
        this.opcodeToMnemonic.put("010", "ADD");
        this.opcodeToMnemonic.put("011", "SUB");
        this.opcodeToMnemonic.put("100", "JMP");
        this.opcodeToMnemonic.put("101", "JC");
        this.opcodeToMnemonic.put("111", "HLT");
        
        this.regToMnemonic = new TreeMap<String,String>();
        this.regToMnemonic.put("0", "RA");
        this.regToMnemonic.put("1", "RB");
        
        this.ccToMnemonic = new TreeMap<String,String>();
        this.ccToMnemonic.put("0", "Z");
        this.ccToMnemonic.put("1", "N");
    }
    
    public String getOpcode() {
        return this.opcodeToMnemonic.get(this.binWord.substring(0, 3));
    }
    
    public String getReg() {
        return this.regToMnemonic.get(this.binWord.substring(3, 4));
    }
    
    public String getCC() {
        return this.ccToMnemonic.get(this.binWord.substring(3, 4));
    }
    
    public Integer getMemAddress() {
        return Integer.parseInt(this.binWord.substring(4,8), 2);
    }
    
    public String parseAssembly() {
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
        return this.getOpcode().equals("JMP") || this.getOpcode().equals("JC");
    }
    
    public boolean isCondJumpInstruction() {
        return this.getOpcode().equals("JC");
    }
    
    public boolean isRegInstruction() {
        return this.getOpcode().equals("LDR") || this.getOpcode().equals("STR") || this.getOpcode().equals("ADD") || this.getOpcode().equals("SUB");
    }
    
    public Integer getWord() {
        return word;
    }

    public void setWord(Integer word) {
        this.word = word;
    }

    public String getPcIsHere() {
        return (this.pcIsHere) ? "*" : "";
    }

    public void setPcIsHere(Boolean pcIsHere) {
        this.pcIsHere = pcIsHere;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public String getHexWord() {
        return hexWord;
    }

    public void setHexWord(String hexWord) {
        this.assigned = true;
        this.hexWord = hexWord;
        this.word = Integer.parseInt(hexWord, 16);
        this.binWord = this.parseBinWord();
        this.assembly = this.parseAssembly();
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getBinWord() {
        return binWord;
    }

    public void setBinWord(String binWord) {
        this.binWord = binWord;
    }
    
    public String toString() {
        return this.assembly;
    }  
    
}

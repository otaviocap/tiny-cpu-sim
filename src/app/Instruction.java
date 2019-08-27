package app;

public class Instruction {
    private byte word;
    private Boolean pcIsHere;
    private Integer address;
    private String hexWord;
    private String assembly;

    public byte getWord() {
        return word;
    }

    public void setWord(byte word) {
        this.word = word;
    }

    public Boolean getPcIsHere() {
        return pcIsHere;
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
        this.hexWord = hexWord;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }
    
    public Instruction(byte word, Integer address) {
        this.word = word;
        this.address = address;
        if(this.address == 0) {
            this.pcIsHere = true;
        }
        this.hexWord = this.parseHexWord();
        //this.assembly = this.parseAssembly();
    }

    private String parseHexWord() {
        return String.format("%02x", this.word);
        
    }

    private String parseAssembly() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

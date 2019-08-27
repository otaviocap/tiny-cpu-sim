package app;

public class MemData {

    private byte word;
    private Integer wordInt;
    private String wordStr;
    private Integer address;
    
    MemData(Byte word, int addCount) {
        this.word = word;
        this.wordInt = new Integer(word);
        this.wordStr = wordInt.toString();
        address = addCount;
    }

    public byte getWord() {
        return word;
    }

    public void setWord(byte word) {
        this.word = word;
    }

    public Integer getWordInt() {
        return wordInt;
    }

    public void setWordInt(Integer wordInt) {
        this.wordInt = wordInt;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public String getWordStr() {
        return wordStr;
    }

    public void setWordStr(String wordStr) {
        this.wordStr = wordStr;
    }
    
    
    
}

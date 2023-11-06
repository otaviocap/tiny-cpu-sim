package tinycpu.simulator.Engine.Memory;

public class MemData {
    private Integer address;
    private byte word;
    private Integer wordInt;
    private String wordStr;

    MemData(Byte word, int addCount) {
        this.word = word;
        this.wordInt = Integer.valueOf(word);
        this.wordStr = wordInt.toString();
        address = addCount;
    }

    public byte getWord() {
        return word;
    }
    
    @SuppressWarnings("unused")
    public void setWord(byte word) {
        this.word = word;
    }

    public Integer getWordInt() {
        return wordInt;
    }

    public void setWordInt(Integer wordInt) {
        this.wordInt = wordInt;
        this.wordStr = wordInt.toString();
        this.word = Byte.valueOf(wordStr, 10);
    }

    public Integer getAddress() {
        return address;
    }

    @SuppressWarnings("unused")
    public void setAddress(Integer address) {
        this.address = address;
    }

    @SuppressWarnings("unused")
    public String getWordStr() {
        return wordStr;
    }

    public void setWordStr(String wordStr) {
        this.wordStr = wordStr;
        this.wordInt = Integer.valueOf(wordStr);
        this.word = Byte.valueOf(wordStr, 10);
    }

    public String toString() {
        return this.address + " " + this.wordStr + "\n";
    }
}

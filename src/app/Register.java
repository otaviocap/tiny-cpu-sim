package app;

public class Register {
    private Integer content;
    private Integer bitWidht;
    private Integer minValue, maxValue;
    
    public Register(Integer content, Integer bitWidth) {
        this.content = content;
        this.bitWidht = bitWidth;
        this.minValue = - (int) Math.pow(2, bitWidth-1);
        this.maxValue = (int) Math.pow(2, bitWidth-1) - 1;
    }
    
    public void add(Integer value) {
        Integer partialSum = this.content + value;
        if(partialSum > this.maxValue) {
            Integer dif = partialSum - this.maxValue;
            this.content = this.minValue + dif;
        }
        else {
            this.content = partialSum;
        }
    }
    
    public void sub(Integer value) {
        Integer partialSub = this.content - value;
        if(partialSub < this.minValue) {
            Integer dif = this.maxValue - partialSub;
            this.content = this.maxValue - dif;
        }
        else {
            this.content = partialSub;
        }
    }

    public Integer getContent() {
        return content;
    }

    public void setContent(Integer content) {
        this.content = content;
    }

    public Integer getBitWidht() {
        return bitWidht;
    }

    public void setBitWidht(Integer bitWidht) {
        this.bitWidht = bitWidht;
    }
    
    public String getHexContent() {
        return Integer.toHexString(this.content);
    }
    
}

package tinycpu.simulator.Engine.Memory;

public class Register {
    private final Integer minValue, maxValue;
    private Integer content;
    private Integer bitWidth;

    public Register(Integer content, Integer bitWidth) {
        this.content = content;
        this.bitWidth = bitWidth;
        this.minValue = -(int) Math.pow(2, bitWidth - 1);
        this.maxValue = (int) Math.pow(2, bitWidth - 1) - 1;
    }

    public void add(Integer value) {
        Integer partialSum = this.content + value;
        if (partialSum > this.maxValue) {
            Integer dif = partialSum - this.maxValue;
            this.content = this.minValue + dif;
        } else {
            this.content = partialSum;
        }
    }

    public void sub(Integer value) {
        Integer partialSub = this.content - value;
        if (partialSub < this.minValue) {
            Integer dif = this.maxValue - partialSub;
            this.content = this.maxValue - dif;
        } else {
            this.content = partialSub;
        }
    }

    public Integer getContent() {
        return content;
    }

    public void setContent(Integer content) {
        this.content = content;
    }

    @SuppressWarnings("unused")
    public Integer getBitWidth() {
        return bitWidth;
    }

    @SuppressWarnings("unused")
    public void setBitWidth(Integer bitWidth) {
        this.bitWidth = bitWidth;
    }

    public String getHexContent() {
        return Integer.toHexString(this.content);
    }

}

package shu.xai.sdc.entity;

public class Element {
    private Integer atomicNumber; // 原子序数
    private String elementName; // 元素名称

    // Getter 和 Setter 方法
    public Integer getAtomicNumber() {
        return atomicNumber;
    }

    public void setAtomicNumber(Integer atomicNumber) {
        this.atomicNumber = atomicNumber;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
}

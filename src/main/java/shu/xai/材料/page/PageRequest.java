package shu.xai.材料.page;

public class PageRequest {
    private int page;        // 修改为小写字母开头
    private int pageSize;    // 修改为小写字母开头
    private String value1;   // 保持与前端一致
    private String value;
    private String radio;

    // Getter 和 Setter 方法
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }
}

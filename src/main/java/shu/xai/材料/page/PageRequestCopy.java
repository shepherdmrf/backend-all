package shu.xai.材料.page;

public class PageRequestCopy {
    private int page;
    private int pageSize;
    private String value1;
    private String value2;

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    // 无参构造函数
    public PageRequestCopy() {
    }

    public PageRequestCopy(int page, int pageSize, String value1, String value2) {
        this.page = page;
        this.pageSize = pageSize;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "PageRequestCopy{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", value1='" + value1 + '\'' +
                ", value2='" + value2 + '\'' +
                '}';
    }

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
}
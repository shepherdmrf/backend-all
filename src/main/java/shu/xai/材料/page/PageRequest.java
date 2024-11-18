package shu.xai.材料.page;

public class PageRequest {
    private int page;
    private int pageSize;

    // 无参构造函数
    public PageRequest() {
    }

    // 带参数的构造函数
    public PageRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
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
package shu.xai.材料.page;

public class PageRequestCopy {
    private int Page;
    private int PageSize;
    private String Value1;
    private int Split;
    private int Repeat;
    private int Iteration;

    public int getPage() {
        return Page;
    }

    public void setPage(int page) {
        Page = page;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public String getValue1() {
        return Value1;
    }

    public void setValue1(String value1) {
        Value1 = value1;
    }

    public int getSplit() {
        return Split;
    }

    public void setSplit(int split) {
        Split = split;
    }

    public int getRepeat() {
        return Repeat;
    }

    public void setRepeat(int repeat) {
        Repeat = repeat;
    }

    public int getIteration() {
        return Iteration;
    }

    public void setIteration(int iteration) {
        Iteration = iteration;
    }
}
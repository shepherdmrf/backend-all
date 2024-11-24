package shu.xai.dataAnalysis.entity;

import java.util.List;


public class PageResult<T> {

    private List<T> list; // 当前页数据
    private int total;    // 总记录数

    // Getter 和 Setter 方法
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // 构造函数（可选）
    public PageResult() {
    }

    public PageResult(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }
}



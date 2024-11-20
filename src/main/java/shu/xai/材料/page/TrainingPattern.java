package shu.xai.材料.page;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TrainingPattern {
    private int pattern;
    private List<List<Integer>> value;

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public List<List<Integer>> getValue() {
        return value;
    }

    public void setValue(List<List<Integer>> value) {
        this.value = value;
    }
}

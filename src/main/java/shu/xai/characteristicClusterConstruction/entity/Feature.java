package shu.xai.characteristicClusterConstruction.entity;

public class Feature {

    private String positive_kernel;
    private String negative_kernel;

    public String getPositive_kernel() {
        return positive_kernel;
    }

    public void setPositive_kernel(String positive_kernel) {
        this.positive_kernel = positive_kernel;
    }

    public String getNegative_kernel() {
        return negative_kernel;
    }

    public void setNegative_kernel(String negative_kernel) {
        this.negative_kernel = negative_kernel;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "positive_kernel='" + positive_kernel + '\'' +
                ", negative_kernel='" + negative_kernel + '\'' +
                '}';
    }
}

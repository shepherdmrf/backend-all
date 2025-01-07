package shu.xai.characteristicClusterConstruction.entity;

public class KeyKnowledgeFeature {
    private int id;
    private String feature;
    private String CV_RMSE;
    private String Test_RMSE;
    private String Test_R2;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFeatures() {
        return feature;
    }

    public void setFeatures(String features) {
        this.feature = features;
    }

    public String getCV_RMSE() {
        return CV_RMSE;
    }

    public void setCV_RMSE(String cvRmse) {
        this.CV_RMSE = cvRmse;
    }

    public String getTest_RMSE() {
        return Test_RMSE;
    }

    public void setTest_RMSE(String rmse) {
        this.Test_RMSE = rmse;
    }

    public String getTest_R2() {
        return Test_R2;
    }

    public void setTest_R2(String r2) {
        this.Test_R2 = r2;
    }

}

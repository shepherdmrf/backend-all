package shu.xai.characteristicClusterConstruction.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExcelRowData {
    private int id;
    private String features;
    private String length;
    private String score;
    private String cvRmse;
    private String rmse;
    private String r2;
    private String time;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCvRmse() {
        return cvRmse;
    }

    public void setCvRmse(String cvRmse) {
        this.cvRmse = cvRmse;
    }

    public String getRmse() {
        return rmse;
    }

    public void setRmse(String rmse) {
        this.rmse = rmse;
    }

    public String getR2() {
        return r2;
    }

    public void setR2(String r2) {
        this.r2 = r2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ExcelRowData{" +
                "features='" + features + '\'' +
                ", length='" + length + '\'' +
                ", score='" + score + '\'' +
                ", cvRmse=" + cvRmse +
                ", rmse='" + rmse + '\'' +
                ", r2=" + r2 +
                ", time=" + time +
                '}';
    }
}


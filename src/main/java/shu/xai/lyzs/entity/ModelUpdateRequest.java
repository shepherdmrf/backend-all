package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelUpdateRequest {
    private int id;

    @JsonProperty("model_name")
    private String modelName;

    private String fitness;

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String model_name) {
        this.modelName = model_name;
    }

    public String getFitness() {
        return fitness;
    }

    public void setFitness(String fitness) {
        this.fitness = fitness;
    }
}

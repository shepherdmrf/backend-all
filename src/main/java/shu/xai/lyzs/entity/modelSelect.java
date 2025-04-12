package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "sys_fitness_model")
public class modelSelect {

    @Id
    @JsonProperty("id")
    private int id;

    @JsonProperty("ath_id")
    private String athId;

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("fitness")
    private String fitness;

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAthId() {
        return athId;
    }

    public void setAthId(String athId) {
        this.athId = athId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFitness() {
        return fitness;
    }

    public void setFitness(String fitness) {
        this.fitness = fitness;
    }
}

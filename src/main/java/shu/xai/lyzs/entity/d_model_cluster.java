package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "d_model_cluster")
public class d_model_cluster {
    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("fitness")
    private String fitness;
}

package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "data_cluster")
public class data_Cluster {
    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("abscissa")
    private String abscissa;

    @JsonProperty("ordinate")
    private String ordinate;

    @JsonProperty("pc3")
    private String pc3;

    @JsonProperty("category")
    private String category;

    @JsonProperty("model_name")
    private String modelName;
}

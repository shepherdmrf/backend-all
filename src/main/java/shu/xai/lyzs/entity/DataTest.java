package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "data_test")
public class DataTest {
    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("realh")
    private String realh;

    @JsonProperty("predh")
    private String predh;


}

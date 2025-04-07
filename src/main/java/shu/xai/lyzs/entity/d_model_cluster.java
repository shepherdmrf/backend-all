package shu.xai.lyzs.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "d_model_cluster")
public class d_model_cluster {
    @Id
    private Long id;
    private String model_name;
    private String fitness;
}

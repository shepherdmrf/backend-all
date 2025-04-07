package shu.xai.lyzs.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "data_cluster")
public class data_Cluster {
    @Id
    private Long id;
    private String abscissa;
    private String ordinate;
    private String pc3;
    private String category;
    private String model_name;
}

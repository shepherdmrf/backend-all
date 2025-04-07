package shu.xai.lyzs.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "data_test")
public class DataTest {
    @Id
    private Long id;
    private String realh;
    private String predh;


}

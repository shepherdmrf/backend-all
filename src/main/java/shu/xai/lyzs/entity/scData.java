package shu.xai.lyzs.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "data_sc")
public class scData {
    @Id
    private Long id;

//    private String category;

    private String sc;

    private String dbi;
    private String chi;
}

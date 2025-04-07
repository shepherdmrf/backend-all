package shu.xai.lyzs.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "data_cad")
public class DataCad {
//    @Id
//    private Long id;
    private String cluster2;
    private String cluster3;
    private String cluster4;
    private String cluster5;
    private String cluster6;
    private String cluster7;
    private String cluster8;
    private String cluster9;
    private String cluster10;
    private String cluster11;

}

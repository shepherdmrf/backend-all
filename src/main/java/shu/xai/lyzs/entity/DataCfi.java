package shu.xai.lyzs.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "data_cfi")
public class DataCfi {
    @Id
    private Long id;
    private String Ni_wt;
    private String Ru_wt;
    private String Re_wt;
    private String Co_wt;
    private String Al_wt;
    private String Ti_wt;
    private String W_wt;
    private String Mo_wt;
    private String Cr_wt;
    private String Ta_wt;
    private String C_wt;
    private String B_wt;
    private String Y_wt;
    private String Nb_wt;
    private String Hf_wt;
    private String stti;
    private String fsatti;
    private String ssatti;
    private String stte;
    private String fsatte;
    private String ssatte;
    private String temperature;
    private String applied_Stress;
}

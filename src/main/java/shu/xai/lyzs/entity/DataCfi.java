package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "data_cfi")
public class DataCfi {

    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("Ni_wt")
    private String niWt;

    @JsonProperty("Ru_wt")
    private String ruWt;

    @JsonProperty("Re_wt")
    private String reWt;

    @JsonProperty("Co_wt")
    private String coWt;

    @JsonProperty("Al_wt")
    private String alWt;

    @JsonProperty("Ti_wt")
    private String tiWt;

    @JsonProperty("W_wt")
    private String wWt;

    @JsonProperty("Mo_wt")
    private String moWt;

    @JsonProperty("Cr_wt")
    private String crWt;

    @JsonProperty("Ta_wt")
    private String taWt;

    @JsonProperty("C_wt")
    private String cWt;

    @JsonProperty("B_wt")
    private String bWt;

    @JsonProperty("Y_wt")
    private String yWt;

    @JsonProperty("Nb_wt")
    private String nbWt;

    @JsonProperty("Hf_wt")
    private String hfWt;

    @JsonProperty("stti")
    private String stti;

    @JsonProperty("fsatti")
    private String fsatti;

    @JsonProperty("ssatti")
    private String ssatti;

    @JsonProperty("stte")
    private String stte;

    @JsonProperty("fsatte")
    private String fsatte;

    @JsonProperty("ssatte")
    private String ssatte;

    @JsonProperty("temperature")
    private String temperature;

    @JsonProperty("applied_Stress")
    private String appliedStress;
}

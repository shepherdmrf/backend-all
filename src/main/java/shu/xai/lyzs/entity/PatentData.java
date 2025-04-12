package shu.xai.lyzs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PatentData {
    @JsonProperty("Alloy_Index")
    private String alloyIndex;

    @JsonProperty("Ni_wt")
    private String niWt;

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

    @JsonProperty("solution_treatment_time")
    private String solutionTreatmentTime;

    @JsonProperty("first_step_aging_treatment_time")
    private String firstStepAgingTreatmentTime;

    @JsonProperty("second_step_aging_treatment_time")
    private String secondStepAgingTreatmentTime;

    @JsonProperty("solution_treatment_temperature")
    private String solutionTreatmentTemperature;

    @JsonProperty("first_step_aging_treatment_temperature")
    private String firstStepAgingTreatmentTemperature;

    @JsonProperty("second_step_aging_treatment_temperature")
    private String secondStepAgingTreatmentTemperature;

    @JsonProperty("temperature")
    private String temperature;

    @JsonProperty("applied_Stress")
    private String appliedStress;

    @JsonProperty("stackingEnergy")
    private String stackingEnergy;

    @JsonProperty("DL")
    private String DL;

    @JsonProperty("G")
    private String G;

    @JsonProperty("L")
    private String L;

    @JsonProperty("Ni3Al_fraction")
    private String ni3AlFraction;

    @JsonProperty("creep_life")
    private String creepLife;

    @JsonProperty("source")
    private String source;
}

package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "data_all_cluster")
public class data_all_Cluster {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("ni_wt")
    private String niWt;

    @JsonProperty("ru_wt")
    private String ruWt;

    @JsonProperty("re_wt")
    private String reWt;

    @JsonProperty("co_wt")
    private String coWt;

    @JsonProperty("al_wt")
    private String alWt;

    @JsonProperty("ti_wt")
    private String tiWt;

    @JsonProperty("w_wt")
    private String wWt;

    @JsonProperty("mo_wt")
    private String moWt;

    @JsonProperty("cr_wt")
    private String crWt;

    @JsonProperty("ta_wt")
    private String taWt;

    @JsonProperty("c_wt")
    private String cWt;

    @JsonProperty("b_wt")
    private String bWt;

    @JsonProperty("y_wt")
    private String yWt;

    @JsonProperty("nb_wt")
    private String nbWt;

    @JsonProperty("hf_wt")
    private String hfWt;

    @JsonProperty("solution_treatment_time")
    private String solutionTreatmentTime;

    @JsonProperty("fst_step_aging_treatment_time")
    private String fstStepAgingTreatmentTime;

    @JsonProperty("snd_step_aging_treatment_time")
    private String sndStepAgingTreatmentTime;

    @JsonProperty("solution_treatment_temperature")
    private String solutionTreatmentTemperature;

    @JsonProperty("fst_step_aging_treatment_temperature")
    private String fstStepAgingTreatmentTemperature;

    @JsonProperty("snd_step_aging_treatment_temperature")
    private String sndStepAgingTreatmentTemperature;

    @JsonProperty("temperature")
    private String temperature;

    @JsonProperty("applied_Stress")
    private String appliedStress;

    @JsonProperty("creep_life")
    private String creepLife;

    @JsonProperty("category")
    private String category;

    @JsonProperty("model_name")
    private String modelName;
}

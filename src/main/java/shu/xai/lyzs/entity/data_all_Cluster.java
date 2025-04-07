package shu.xai.lyzs.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;

@Data
@Table(name = "data_all_cluster")
public class data_all_Cluster {
    @Id
    private String id;
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
    private String solution_treatment_time;
    private String fst_step_aging_treatment_time;
    private String snd_step_aging_treatment_time;
    private String solution_treatment_temperature;
    private String fst_step_aging_treatment_temperature;
    private String snd_step_aging_treatment_temperature;
    private String temperature;
    private String applied_Stress;
    private String creep_life;
    private String category;
    private String model_name;
}

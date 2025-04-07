package shu.xai.lyzs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class PatentData {
    private String Alloy_Index;
    private String Ni_wt;
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
    private String first_step_aging_treatment_time;
    private String second_step_aging_treatment_time;
    private String solution_treatment_temperature;
    private String first_step_aging_treatment_temperature;
    private String second_step_aging_treatment_temperature;
    private String temperature;
    private String applied_Stress;
    private String stackingEnergy;
    private String DL;
    private String G;
    private String L;
    private String Ni3Al_fraction;
    private String creep_life;
    private String source;
}

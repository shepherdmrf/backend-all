package shu.xai.dataAnalysis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Occu_6b", "Occu_18e", "Occu_36f", "C_Na",
        "Occu_M1", "Occu_M2", "EN_M1", "EN_M2",
        "avg_EN_M", "Radius_M1", "Radius_M2", "avg_Radius_M",
        "Valence_M1", "Valence_M2", "avg_Valence_M",
        "Occu_X1", "Occu_X2", "EN_X1", "EN_X2",
        "avg_EN_X", "Radius_X1", "Radius_X2", "avg_Radius_X",
        "Valence_X1", "Valence_X2", "avg_Valence_X",
        "a", "c", "V", "V_MO6", "V_XO4",
        "V_Na1_O6", "V_Na2_O8", "V_Na3_O5",
        "BT2", "BT1", "Min_BT", "RT",
        "Entropy_6b", "Entropy_18e", "Entropy_36f",
        "Entropy_Na", "Entropy_M", "Entropy_X",
        "T", "BVSE_energy_barrier"
})
public class MaterialData {
//    @JsonProperty("Occu_6b")
    private Double Occu_6b;
//    @JsonProperty("Occu_18e")
    private Double Occu_18e;
//    @JsonProperty("Occu_36f")
    private Double Occu_36f;
//    @JsonProperty("C_Na")
    private Double C_Na;
//    @JsonProperty("Occu_M1")
    private Double Occu_M1;
//    @JsonProperty("Occu_M2")
    private Double Occu_M2;
//    @JsonProperty("EN_M1")
    private Double EN_M1;
//    @JsonProperty("EN_M2")
    private Double EN_M2;
//    @JsonProperty("avg_EN_M")
    private Double avg_EN_M;
//    @JsonProperty("Radius_M1")
    private Double Radius_M1;
//    @JsonProperty("Radius_M2")
    private Double Radius_M2;
//    @JsonProperty("avg_Radius_M")
    private Double avg_Radius_M;
//    @JsonProperty("Valence_M1")
    private Double Valence_M1;
//    @JsonProperty("Valence_M2")
    private Double Valence_M2;
//    @JsonProperty("avg_Valence_M")
    private Double avg_Valence_M;
//    @JsonProperty("Occu_X1")
    private Double Occu_X1;
//    @JsonProperty("Occu_X2")
    private Double Occu_X2;
//    @JsonProperty("EN_X1")
    private Double EN_X1;
//    @JsonProperty("EN_X2")
    private Double EN_X2;
//    @JsonProperty("avg_EN_X")
    private Double avg_EN_X;
//    @JsonProperty("Radius_X1")
    private Double Radius_X1;
//    @JsonProperty("Radius_X2")
    private Double Radius_X2;
//    @JsonProperty("avg_Radius_X")
    private Double avg_Radius_X;
//    @JsonProperty("Valence_X1")
    private Double Valence_X1;
//    @JsonProperty("Valence_X2")
    private Double Valence_X2;
//    @JsonProperty("avg_Valence_X")
    private Double avg_Valence_X;
    @JsonProperty("a")
    private Double a;
    @JsonProperty("c")
    private Double c;
//    @JsonProperty("V")
    private Double V;
//    @JsonProperty("V_MO6")
    private Double V_MO6;
//    @JsonProperty("V_XO4")
    private Double V_XO4;
//    @JsonProperty("V_Na1_O6")
    private Double V_Na1_O6;
//    @JsonProperty("V_Na2_O8")
    private Double V_Na2_O8;
//    @JsonProperty("V_Na3_O5")
    private Double V_Na3_O5;
//    @JsonProperty("BT2")
    private Double BT2;
//    @JsonProperty("BT1")
    private Double BT1;
//    @JsonProperty("Min_BT")
    private Double Min_BT;
//    @JsonProperty("RT")
    private Double RT;
//    @JsonProperty("Entropy_6b")
    private Double Entropy_6b;
//    @JsonProperty("Entropy_18e")
    private Double Entropy_18e;
//    @JsonProperty("Entropy_36f")
    private Double Entropy_36f;
//    @JsonProperty("Entropy_Na")
    private Double Entropy_Na;
//    @JsonProperty("Entropy_M")
    private Double Entropy_M;
//    @JsonProperty("Entropy_X")
    private Double Entropy_X;
//    @JsonProperty("T")
    private Double T;
//    @JsonProperty("BVSE_energy_barrier")
    private Double BVSE_energy_barrier;


    public Double getOccu_6b() {
        return Occu_6b;
    }

    public void setOccu_6b(Double occu_6b) {
        Occu_6b = occu_6b;
    }

    public Double getOccu_18e() {
        return Occu_18e;
    }

    public void setOccu_18e(Double occu_18e) {
        Occu_18e = occu_18e;
    }

    public Double getOccu_36f() {
        return Occu_36f;
    }

    public void setOccu_36f(Double occu_36f) {
        Occu_36f = occu_36f;
    }

    public Double getC_Na() {
        return C_Na;
    }

    public void setC_Na(Double c_Na) {
        C_Na = c_Na;
    }

    public Double getOccu_M1() {
        return Occu_M1;
    }

    public void setOccu_M1(Double occu_M1) {
        Occu_M1 = occu_M1;
    }

    public Double getOccu_M2() {
        return Occu_M2;
    }

    public void setOccu_M2(Double occu_M2) {
        Occu_M2 = occu_M2;
    }

    public Double getEN_M1() {
        return EN_M1;
    }

    public void setEN_M1(Double eN_M1) {
        EN_M1 = eN_M1;
    }

    public Double getEN_M2() {
        return EN_M2;
    }

    public void setEN_M2(Double eN_M2) {
        EN_M2 = eN_M2;
    }

    public Double getAvg_EN_M() {
        return avg_EN_M;
    }

    public void setAvg_EN_M(Double avg_EN_M) {
        this.avg_EN_M = avg_EN_M;
    }

    public Double getRadius_M1() {
        return Radius_M1;
    }

    public void setRadius_M1(Double radius_M1) {
        Radius_M1 = radius_M1;
    }

    public Double getRadius_M2() {
        return Radius_M2;
    }

    public void setRadius_M2(Double radius_M2) {
        Radius_M2 = radius_M2;
    }

    public Double getAvg_Radius_M() {
        return avg_Radius_M;
    }

    public void setAvg_Radius_M(Double avg_Radius_M) {
        this.avg_Radius_M = avg_Radius_M;
    }

    public Double getValence_M1() {
        return Valence_M1;
    }

    public void setValence_M1(Double valence_M1) {
        Valence_M1 = valence_M1;
    }

    public Double getValence_M2() {
        return Valence_M2;
    }

    public void setValence_M2(Double valence_M2) {
        Valence_M2 = valence_M2;
    }

    public Double getAvg_Valence_M() {
        return avg_Valence_M;
    }

    public void setAvg_Valence_M(Double avg_Valence_M) {
        this.avg_Valence_M = avg_Valence_M;
    }

    public Double getOccu_X1() {
        return Occu_X1;
    }

    public void setOccu_X1(Double occu_X1) {
        Occu_X1 = occu_X1;
    }

    public Double getOccu_X2() {
        return Occu_X2;
    }

    public void setOccu_X2(Double occu_X2) {
        Occu_X2 = occu_X2;
    }

    public Double getEN_X1() {
        return EN_X1;
    }

    public void setEN_X1(Double eN_X1) {
        EN_X1 = eN_X1;
    }

    public Double getEN_X2() {
        return EN_X2;
    }

    public void setEN_X2(Double eN_X2) {
        EN_X2 = eN_X2;
    }

    public Double getAvg_EN_X() {
        return avg_EN_X;
    }

    public void setAvg_EN_X(Double avg_EN_X) {
        this.avg_EN_X = avg_EN_X;
    }

    public Double getRadius_X1() {
        return Radius_X1;
    }

    public void setRadius_X1(Double radius_X1) {
        Radius_X1 = radius_X1;
    }

    public Double getRadius_X2() {
        return Radius_X2;
    }

    public void setRadius_X2(Double radius_X2) {
        Radius_X2 = radius_X2;
    }

    public Double getAvg_Radius_X() {
        return avg_Radius_X;
    }

    public void setAvg_Radius_X(Double avg_Radius_X) {
        this.avg_Radius_X = avg_Radius_X;
    }

    public Double getValence_X1() {
        return Valence_X1;
    }

    public void setValence_X1(Double valence_X1) {
        Valence_X1 = valence_X1;
    }

    public Double getValence_X2() {
        return Valence_X2;
    }

    public void setValence_X2(Double valence_X2) {
        Valence_X2 = valence_X2;
    }

    public Double getAvg_Valence_X() {
        return avg_Valence_X;
    }

    public void setAvg_Valence_X(Double avg_Valence_X) {
        this.avg_Valence_X = avg_Valence_X;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }

    public Double getV() {
        return V;
    }

    public void setV(Double v) {
        V = v;
    }

    public Double getV_MO6() {
        return V_MO6;
    }

    public void setV_MO6(Double v_MO6) {
        V_MO6 = v_MO6;
    }

    public Double getV_XO4() {
        return V_XO4;
    }

    public void setV_XO4(Double v_XO4) {
        V_XO4 = v_XO4;
    }

    public Double getV_Na1_O6() {
        return V_Na1_O6;
    }

    public void setV_Na1_O6(Double v_Na1_O6) {
        this.V_Na1_O6 = v_Na1_O6;
    }

    public Double getV_Na2_O8() {
        return V_Na2_O8;
    }

    public void setV_Na2_O8(Double v_Na2_O8) {
        this.V_Na2_O8 = v_Na2_O8;
    }

    public Double getV_Na3_O5() {
        return V_Na3_O5;
    }

    public void setV_Na3_O5(Double v_Na3_O5) {
        V_Na3_O5 = v_Na3_O5;
    }

    public Double getBT2() {
        return BT2;
    }

    public void setBT2(Double bt2) {
        BT2 = bt2;
    }

    public Double getBT1() {
        return BT1;
    }

    public void setBT1(Double bt1) {
        BT1 = bt1;
    }

    public Double getMin_BT() {
        return Min_BT;
    }

    public void setMin_BT(Double min_BT) {
        Min_BT = min_BT;
    }

    public Double getRT() {
        return RT;
    }

    public void setRT(Double rt) {
        RT = rt;
    }

    public Double getEntropy_6b() {
        return Entropy_6b;
    }

    public void setEntropy_6b(Double entropy_6b) {
        Entropy_6b = entropy_6b;
    }

    public Double getEntropy_18e() {
        return Entropy_18e;
    }

    public void setEntropy_18e(Double entropy_18e) {
        Entropy_18e = entropy_18e;
    }

    public Double getEntropy_36f() {
        return Entropy_36f;
    }

    public void setEntropy_36f(Double entropy_36f) {
        Entropy_36f = entropy_36f;
    }

    public Double getEntropy_Na() {
        return Entropy_Na;
    }

    public void setEntropy_Na(Double entropy_Na) {
        Entropy_Na = entropy_Na;
    }

    public Double getEntropy_M() {
        return Entropy_M;
    }

    public void setEntropy_M(Double entropy_M) {
        Entropy_M = entropy_M;
    }

    public Double getEntropy_X() {
        return Entropy_X;
    }

    public void setEntropy_X(Double entropy_X) {
        Entropy_X = entropy_X;
    }

    public Double getT() {
        return T;
    }

    public void setT(Double t) {
        T = t;
    }

    public Double getBVSE_energy_barrier() {
        return BVSE_energy_barrier;
    }

    public void setBVSE_energy_barrier(Double bvse_energy_barrier) {
        BVSE_energy_barrier = bvse_energy_barrier;
    }
}

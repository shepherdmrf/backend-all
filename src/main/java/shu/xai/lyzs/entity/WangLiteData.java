package shu.xai.lyzs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WangLiteData {

    @JsonProperty("d_abstract")
    private String dAbstract;

    @JsonProperty("d_submission_name")
    private String dSubmissionName;

    @JsonProperty("d_submission_unit")
    private String dSubmissionUnit;

    @JsonProperty("d_proofreader")
    private String dProofreader;

    @JsonProperty("d_size_m")
    private Integer dSizeM;

    @JsonProperty("telephoneNumber")
    private String telephoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("d_size_n")
    private Integer dSizeN;

    @JsonProperty("keywords")
    private String keywords;

    @JsonProperty("contact_address")
    private String contactAddress;

    @JsonProperty("d_submission_date")
    private String dSubmissionDate;

    @JsonProperty("domain_type")
    private String domainType;

    @JsonProperty("area_type")
    private String areaType;

    @JsonProperty("d_data_description_id")
    private String dDataDescriptionId;

    @JsonProperty("record_name")
    private String recordName;

    @JsonProperty("related_literature")
    private String relatedLiterature;

    @JsonProperty("file_attachment")
    private String fileAttachment;

    @JsonProperty("op_user")
    private String opUser;


}

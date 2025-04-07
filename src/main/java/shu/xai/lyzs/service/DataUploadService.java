package shu.xai.lyzs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * service层接口
 * Created by yuziyi on 2021/6/20.
 */
public interface DataUploadService {
    // 实验数据
    JSONObject addNewData(String d_abstract, Integer d_size_m, Integer d_size_n, String keywords, String domain_type, String area_type, String d_submission_name, String d_submission_unit, String d_proofreader, String email, String telephoneNumber, String contact_address, String op_user, String record_name, String file_attachment, String dataset_id);
    JSONObject addNewExp(String data_sort, String decide_type, String material_trademark, String m_name, String expcon_name, String exp_parasetting, String exp_device_name, String data_source, String dataset_id);
    // 计算方法
    JSONObject addNewAlgo(String algoName, String cal_field, String algoAbstract, String algoSubmission_name, String algoType, String algoLocation, String algoDescription, String realUser);
    String getalgorithmDescriptionPath(String algoId);

    // 文献
    JSONObject addNewLiterature(String titleE, String authorE, String l_abstractE, String keywordsE, String title, String author, String l_abstract, String keywords, String file_attachment,String fileName, String doi, String material_method, String ml_method, String research_institute, String publish_date, Integer start_page, Integer end_page, Integer volume, Integer issue, String reference_type, String realUser);
    // 专利
    JSONObject addNewPatent(String title, String author, String l_abstract,
                            String keywords, String publish_date,
                            String patent_number, String patent_type, String patent_institute,
                            String patent_region, String file_attachment, String patent_submission_name);




    int repeatData(String fileName,String type);
    int repeatDataAlgo(String fileName1,String fileName2);

}

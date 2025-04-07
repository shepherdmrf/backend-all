package shu.xai.lyzs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.service.DataUploadService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.CoreUtils;
import shu.xai.sys.utils.ResultUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static shu.xai.sys.utils.CoreUtils.getDateDay;


/**
 * service层实现类 具体功能实现，包括数据库交互
 * Created by yuziyi on 2022/1/24.
 */
@Service("DataUploadService")
public class DataUploadServiceImpl implements DataUploadService {

    @Resource(name = "jdbcTemplatelyzs")
    private JdbcTemplate jdbcTemplatelyzs;


    @Override
    public JSONObject addNewData(String d_abstract, Integer d_size_m, Integer d_size_n, String keywords, String domain_type, String area_type, String d_submission_name, String d_submission_unit, String d_proofreader, String email, String telephoneNumber, String contact_address,String op_user, String record_name, String file_attachment, String dataset_id) {
        JSONObject result=new JSONObject();
        String day1=getDateDay();

        jdbcTemplatelyzs.update("INSERT INTO data_description(d_abstract, d_size_m, d_size_n, keywords, domain_type, area_type, d_submission_name, d_submission_unit, d_proofreader, email, telephoneNumber, contact_address,op_user,record_name, file_attachment,d_data_description_id,d_submission_date) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                d_abstract, d_size_m, d_size_n, keywords, domain_type, area_type, d_submission_name, d_submission_unit, d_proofreader, email, telephoneNumber, contact_address, op_user,record_name,file_attachment,dataset_id,day1);
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }

    @Override
    public JSONObject addNewExp(String data_sort, String decide_type, String material_trademark, String m_name, String expcon_name, String exp_parasetting, String exp_device_name, String data_source, String dataset_id) {
        JSONObject result=new JSONObject();
        jdbcTemplatelyzs.update("INSERT INTO experiment_data(data_sort, decide_type, material_trademark, m_name, expcon_name, exp_parasetting, exp_device_name, data_source,description_id) VALUES(?,?,?,?,?,?,?,?,?)",
                data_sort, decide_type, material_trademark, m_name, expcon_name, exp_parasetting, exp_device_name, data_source,dataset_id);
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }

    /**
     * 添加算法实现
     * @return result 操作状态封装返回
     */
    @Override
    public JSONObject addNewAlgo(String algoName,String cal_field,String algoAbstract,String algoSubmission_name,String algoType,String algoLocation,String algoDescription,String realUser) {
        JSONObject result=new JSONObject();
        String algoDate= CoreUtils.getDateTime();
        String uuid=CoreUtils.getUUID();
        jdbcTemplatelyzs.update("INSERT INTO algorithm(algoName,cal_field,algoLocation,algoDescription,algoAbstract,algoSubmission_name,algoType,submit_user,algoDate,algo_id) VALUES(?,?,?,?,?,?,?,?,?,?)",
                algoName,cal_field,algoLocation,algoDescription,algoAbstract,algoSubmission_name,algoType,realUser,algoDate,uuid);
        result= ResultUtils.commonJSONSuccess(uuid);
        return  result;
    }

    @Override
    public String getalgorithmDescriptionPath(String algoId){

        String path="";
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.algoDescription,t.algoName," +
                "t.algoAbstract from algorithm t where t.algo_id=?",algoId);
        path= String.valueOf(searchResult.get(0).get("algoDescription"));
        return path;
    }

    /**
     * 添加文献
     * @return result 操作状态封装返回
     */
    @Override
    public JSONObject addNewLiterature( String titleE, String authorE,  String l_abstractE, String keywordsE, String title, String author,String l_abstract,String keywords,String file_attachment, String fileName,String doi,String material_method,String ml_method,String research_institute,String publish_date,Integer start_page,Integer end_page,Integer volume,Integer issue,String reference_type,String realUser) {
        JSONObject result=new JSONObject();
        jdbcTemplatelyzs.update("INSERT INTO data_literature(titleE,authorE,l_abstractE,keywordsE,title,author,l_abstract,keywords,file_attachment,doi,material_method,ml_method,research_institute,publish_date,start_page,end_page,volume,issue,reference_type,realUser,file_name) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                titleE,authorE,l_abstractE,keywordsE,title,author,l_abstract,keywords,file_attachment,doi,material_method,ml_method,research_institute,publish_date,String.valueOf(start_page),String.valueOf(end_page),String.valueOf(volume),String.valueOf(issue),reference_type,realUser,fileName);
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }

    /**
     * 添加专利实现
     * @param title 标题（中文）
     * @param author 作者（中文）
     * @param l_abstract 摘要（中文）
     * @param keywords 关键字（中文）
     * @return result 操作状态封装返回
     */
    @Override
    public JSONObject addNewPatent(String title, String author, String l_abstract,
                                   String keywords, String publish_date,
                                   String patent_number, String patent_type, String patent_institute,
                                   String patent_region, String file_attachment, String patent_submission_name) {
        JSONObject result=new JSONObject();
        jdbcTemplatelyzs.update("INSERT INTO data_patent(title,author,l_abstract," +
                        "keywords,publish_date,patent_number,patent_type,patent_institute,patent_region," +
                        "file_attachment,patent_submission_name) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                title,author,l_abstract,keywords,publish_date,patent_number,
                patent_type,patent_institute,patent_region,file_attachment,patent_submission_name);
        //事务测试，可行
//        int i=1/0;
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }

    @Override
    public int repeatData(String fileName, String type) {
        if("literature".equals(type)){
            List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.id from data_literature t where t.file_name=?",fileName);
            return searchResult.size();
        }else if("dataSet".equals(type)){
            List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.d_data_description_id from data_description t where t.record_name=?",fileName);
            return searchResult.size();
        }else{
            List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.id from data_patent t where t.file_attachment LIKE concat('%', ?, '%')",fileName);
            return searchResult.size();
        }

    }

    @Override
    public int repeatDataAlgo(String fileName1, String fileName2) {
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("select  t.algo_id  from algorithm t where t.algoLocation like concat('%', ?, '%') or t.algoDescription like concat('%', ?, '%') ",fileName1,fileName2);
        return searchResult.size();
    }


}

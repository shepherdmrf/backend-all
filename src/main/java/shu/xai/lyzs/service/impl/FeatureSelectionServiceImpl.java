package shu.xai.lyzs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.service.FeatureSelectionService;
import shu.xai.sys.utils.ExcelUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by yuziyi on 2022/10/22.
 */
@Service("FeatureSelectionService")
public class FeatureSelectionServiceImpl implements FeatureSelectionService {

    @Resource(name = "jdbcTemplatelyzs")
    private JdbcTemplate jdbcTemplatelyzs;


    @Override
    public JSONArray getDatasetOP() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t1.description_id,t2.d_abstract,t2.d_submission_name from experiment_data t1 LEFT JOIN data_description t2 on t1.description_id=t2.d_data_description_id");
        for(Map item:searchResult){
            JSONObject alre=new JSONObject();
            alre.put("name",item.get("d_abstract")+"-"+item.get("d_submission_name"));
            alre.put("value",item.get("description_id"));
            result.add(alre);
        }
        return result;
    }

    @Override
    public JSONObject getDatasetInfo(String datasetId) {
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.keywords,t.d_size_m,t.d_size_n,t.domain_type from data_description t where t.d_data_description_id=?",datasetId);
        result.put("d_keywords",searchResult.get(0).get("keywords"));
        result.put("d_m",searchResult.get(0).get("d_size_m"));
        result.put("d_n",searchResult.get(0).get("d_size_n"));
        result.put("d_domain_type",searchResult.get(0).get("domain_type"));
        return result;
    }

    @Override
    public JSONObject getDatasetData(String datasetId) throws IOException {
        JSONObject result=new JSONObject();
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.file_attachment from data_description t where t.d_data_description_id=?",datasetId);
        String file_attachment= String.valueOf(searchResult.get(0).get("file_attachment"));
        String path = System.getProperty("user.dir")+ File.separator +file_attachment;
//        System.out.println(path);
        return ExcelUtils.readExcel(path);


//        return result;
    }

}

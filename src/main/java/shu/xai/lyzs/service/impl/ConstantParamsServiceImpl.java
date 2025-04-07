package shu.xai.lyzs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.service.ConstantParamsService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * Created by yuziyi on 2022/10/22.
 */
@Service("ConstantParamsService")
public class ConstantParamsServiceImpl implements ConstantParamsService {

    @Resource(name = "jdbcTemplatelyzs")
    private JdbcTemplate jdbcTemplatelyzs;

//   计算方法

    /**
     * 查询算法领域参数
     * @return result
     */
    @Override
    public JSONArray getCalFieldOptions() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT a.name,a.value from cal_field a");
        for(Map item:searchResult){
            JSONObject alre=new JSONObject();
            alre.put("name",item.get("name"));
            alre.put("value",item.get("value"));
            result.add(alre);
        }
        return result;
    }

    /**
     * 查询算法类型参数
     * @return result
     */
    @Override
    public JSONArray getAlgoTypeOptions() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT a.name,a.value from algo_type a");
        for(Map item:searchResult){
            JSONObject alre=new JSONObject();
            alre.put("name",item.get("name"));
            alre.put("value",item.get("value"));
            result.add(alre);
        }
        return result;
    }


}

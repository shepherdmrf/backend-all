package shu.xai.demo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shu.xai.demo.service.DemoService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * service层实现类 具体功能实现，包括数据库交互
 * Created by yuziyi on 2022/1/24.
 */
@Service("DemoService")
@Transactional(transactionManager ="TransactionManagersp")
public class DemoServiceImpl implements DemoService {

    @Resource(name = "jdbcTemplatesp")
    private JdbcTemplate jdbcTemplatesp;

    /**
     * 查询专利列表实现
     * @return result 返回专利数据的JSONArray
     */
    @Override
    public JSONArray getPatentList() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplatesp.queryForList("SELECT t.title,t.titleE,t.author,t.authorE,t.l_abstract,t.l_abstractE,t.keywords,t.keywordsE from data_patent t");
        for(Map item:searchResult){
            JSONObject re=new JSONObject();
            re.put("title",item.get("title"));
            re.put("titleE",item.get("titleE"));
            re.put("author",item.get("author"));
            re.put("authorE",item.get("authorE"));
            re.put("l_abstract",item.get("l_abstract"));
            re.put("l_abstractE",item.get("l_abstractE"));
            re.put("keywords",item.get("keywords"));
            re.put("keywordsE",item.get("keywordsE"));
            result.add(re);
        }
        return result;
    }

    /**
     * 添加专利实现
     * @param title 标题（中文）
     * @param titleE 标题（英文）
     * @param author 作者（中文）
     * @param authorE 作者（英文）
     * @param l_abstract 摘要（中文）
     * @param l_abstractE 摘要（英文）
     * @param keywords 关键字（中文）
     * @param keywordsE 关键字（英文）
     * @return result 操作状态封装返回
     */
    @Override
    public JSONObject addNewPatent(String title, String titleE, String author, String authorE, String l_abstract, String l_abstractE, String keywords, String keywordsE) {
        JSONObject result=new JSONObject();
        jdbcTemplatesp.update("INSERT INTO data_patent(title,titleE,author,authorE,l_abstract,l_abstractE,keywords,keywordsE) VALUES(?,?,?,?,?,?,?,?)",
                title,titleE,author,authorE,l_abstract,l_abstractE,keywords,keywordsE);
        result= ResultUtils.commonResultJSON(ResultCodeEnums.SUCCESS.getCode(),ResultCodeEnums.SUCCESS.getMsg());
        return  result;
    }
}

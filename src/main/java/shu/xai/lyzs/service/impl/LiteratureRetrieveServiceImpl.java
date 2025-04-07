package shu.xai.lyzs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.service.LiteratureRetrieveService;
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
@Service("LiteratureRetrieveService")
public class LiteratureRetrieveServiceImpl implements LiteratureRetrieveService {

    @Resource(name = "jdbcTemplatelyzs")
    private JdbcTemplate jdbcTemplatelyzs;

    /**
     * 查询文献列表实现
     * @return result 返回专利数据的JSONArray
     */
    @Override
    public JSONArray getLiteratureList() {
        JSONArray result=new JSONArray();
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.id,t.titleE,t.authorE," +
                "t.l_abstractE,t.keywordsE,t.publish_date,t.reference_type,t.publish_institute," +
                "t.doi,t.file_attachment,t.research_institute,t.material_method,t.ml_method," +
                "t.volume,t.issue,t.start_page,t.end_page,t.related_data_id,t.title,t.author,t.keywords,t.l_abstract from data_literature t");
        for(Map item:searchResult){
            JSONObject re=new JSONObject();
            re.put("id",item.get("id"));
            re.put("titleE",item.get("titleE"));
            re.put("authorE",item.get("authorE"));
            re.put("l_abstractE",item.get("l_abstractE"));
            re.put("keywordsE",item.get("keywordsE"));
            re.put("publish_date",item.get("publish_date"));
            re.put("reference_type",item.get("reference_type"));
            re.put("publish_institute",item.get("publish_institute"));
            re.put("doi",item.get("doi"));
            re.put("file_attachment",item.get("file_attachment"));
            re.put("research_institute",item.get("research_institute"));
            re.put("material_method",item.get("material_method"));
            re.put("ml_method",item.get("ml_method"));
            re.put("volume",item.get("volume"));
            re.put("issue",item.get("issue"));
            re.put("start_page",item.get("start_page"));
            re.put("end_page",item.get("end_page"));
            re.put("related_data_id",item.get("related_data_id"));
            re.put("title",item.get("title"));
            re.put("author",item.get("author"));
            re.put("keywords",item.get("keywords"));
            re.put("l_abstract",item.get("l_abstract"));
            result.add(re);
        }
        return result;
    }


    /**
     * 搜搜文献列表实现
     * @return result 返回专利数据的JSONArray
     */
    @Override
    public JSONArray LiteratureSearchTemp(String literatureSearch) {
        JSONArray result=new JSONArray();
//        List<Map<String,Object>> searchResult=jdbcTemplateSuperAlloy.queryForList("SELECT t.titleE,t.keywordsE," +
//                "t.authorE,t.publish_date,t.reference_type,t.research_institute,t.doi from data_literature t" +
//                "where t.titleE like '%single%'",literatureSearch);
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.id,t.titleE,t.authorE," +
                "t.l_abstractE,t.keywordsE,t.publish_date,t.reference_type,t.publish_institute," +
                "t.doi,t.file_attachment,t.research_institute,t.material_method,t.ml_method," +
                "t.volume,t.issue,t.start_page,t.end_page,t.related_data_id,t.title,t.author,t.keywords,t.l_abstract from data_literature t " +
                "where t.keywordsE like '%"+literatureSearch+"%' or " +
                "t.authorE like '%"+literatureSearch+"%' or " +
                "t.publish_date like '%"+literatureSearch+"%' or " +
                "t.reference_type like '%"+literatureSearch+"%' or " +
                "t.publish_institute like '%"+literatureSearch+"%' or " +
                "t.doi like '%"+literatureSearch+"%' or " +
                "t.titleE like '%"+literatureSearch+"%'");
        if(!searchResult.isEmpty())
        {
            for(Map item:searchResult){
                JSONObject re=new JSONObject();
                re.put("id",item.get("id"));
                re.put("titleE",item.get("titleE"));
                re.put("authorE",item.get("authorE"));
                re.put("l_abstractE",item.get("l_abstractE"));
                re.put("keywordsE",item.get("keywordsE"));
                re.put("publish_date",item.get("publish_date"));
                re.put("reference_type",item.get("reference_type"));
                re.put("publish_institute",item.get("publish_institute"));
                re.put("doi",item.get("doi"));
                re.put("file_attachment",item.get("file_attachment"));
                re.put("research_institute",item.get("research_institute"));
                re.put("material_method",item.get("material_method"));
                re.put("ml_method",item.get("ml_method"));
                re.put("volume",item.get("volume"));
                re.put("issue",item.get("issue"));
                re.put("start_page",item.get("start_page"));
                re.put("end_page",item.get("end_page"));
                re.put("related_data_id",item.get("related_data_id"));
                re.put("title",item.get("title"));
                re.put("author",item.get("author"));
                re.put("keywords",item.get("keywords"));
                re.put("l_abstract",item.get("l_abstract"));
                result.add(re);
            }
        }
        return result;
    }

    /**
     * 高级搜搜文献列表实现
     * @return result 返回专利数据的JSONArray
     */
    @Override
    public JSONArray LiteratureSearchSenior(String authorE, String keywordsE, String titleE, String referenceType) {
        JSONArray result=new JSONArray();
//        List<Map<String,Object>> searchResult=jdbcTemplateSuperAlloy.queryForList("SELECT t.titleE,t.keywordsE," +
//                "t.authorE,t.publish_date,t.reference_type,t.research_institute,t.doi from data_literature t" +
//                "where t.titleE like '%single%'",literatureSearch);
        List<Map<String,Object>> searchResult=jdbcTemplatelyzs.queryForList("SELECT t.id,t.titleE,t.authorE," +
                "t.l_abstractE,t.keywordsE,t.publish_date,t.reference_type,t.publish_institute," +
                "t.doi,t.file_attachment,t.research_institute,t.material_method,t.ml_method," +
                "t.volume,t.issue,t.start_page,t.end_page,t.related_data_id,t.title,t.author,t.keywords,t.l_abstract from data_literature t " +
                "where t.keywordsE like '%"+keywordsE+"%' and " +
                "t.authorE like '%"+authorE+"%' and " +
                "t.reference_type like '%"+referenceType+"%' and " +
                "t.titleE like '%"+titleE+"%'");
        if(!searchResult.isEmpty())
        {
            for(Map item:searchResult){
                JSONObject re=new JSONObject();
                re.put("id",item.get("id"));
                re.put("titleE",item.get("titleE"));
                re.put("authorE",item.get("authorE"));
                re.put("l_abstractE",item.get("l_abstractE"));
                re.put("keywordsE",item.get("keywordsE"));
                re.put("publish_date",item.get("publish_date"));
                re.put("reference_type",item.get("reference_type"));
                re.put("publish_institute",item.get("publish_institute"));
                re.put("doi",item.get("doi"));
                re.put("file_attachment",item.get("file_attachment"));
                re.put("research_institute",item.get("research_institute"));
                re.put("material_method",item.get("material_method"));
                re.put("ml_method",item.get("ml_method"));
                re.put("volume",item.get("volume"));
                re.put("issue",item.get("issue"));
                re.put("start_page",item.get("start_page"));
                re.put("end_page",item.get("end_page"));
                re.put("related_data_id",item.get("related_data_id"));
                re.put("title",item.get("title"));
                re.put("author",item.get("author"));
                re.put("keywords",item.get("keywords"));
                re.put("l_abstract",item.get("l_abstract"));
                result.add(re);
            }
        }
        return result;
    }

    @Override
    public String[] getKeywordsNum(String keyword){
        String keywordsList[]=keyword.split(";");
        String[] valueList=new String[keywordsList.length];
        for(int i=0;i<keywordsList.length;i++){
            int vresult=jdbcTemplatelyzs.queryForObject("SELECT count(1) from data_literature t where t.keywordsE LIKE concat('%', ?, '%')",new Object[]{keywordsList[i].trim()},Integer.class);
            valueList[i]= String.valueOf(vresult);
        }

        return valueList;
    }



}

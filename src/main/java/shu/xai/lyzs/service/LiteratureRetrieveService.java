package shu.xai.lyzs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * service层接口
 * Created by yuziyi on 2021/6/20.
 */
public interface LiteratureRetrieveService {
    //文献检索

    JSONArray getLiteratureList();
    JSONArray LiteratureSearchTemp(String literatureSearch);
    JSONArray LiteratureSearchSenior(String authorE, String keywordsE, String titleE, String referenceType);
    String[] getKeywordsNum(String keyword);

}

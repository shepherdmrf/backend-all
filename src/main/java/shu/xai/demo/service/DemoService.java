package shu.xai.demo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * service层接口
 * Created by yuziyi on 2021/6/20.
 */
public interface DemoService {
    JSONArray getPatentList();
    JSONObject addNewPatent(String title, String titleE, String author, String authorE, String l_abstract, String l_abstractE, String keywords, String keywordsE);

}

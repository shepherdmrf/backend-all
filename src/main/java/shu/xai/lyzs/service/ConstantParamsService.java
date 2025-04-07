package shu.xai.lyzs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * service层接口
 * Created by yuziyi on 2021/6/20.
 */
public interface ConstantParamsService {
    JSONArray getCalFieldOptions();
    JSONArray getAlgoTypeOptions();

}

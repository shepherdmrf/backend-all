package shu.xai.lyzs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * service层接口
 * Created by yuziyi on 2021/6/20.
 */
public interface FeatureSelectionService {
    JSONArray getDatasetOP();
    JSONObject getDatasetInfo(String datasetId);
    JSONObject getDatasetData(String datasetId) throws IOException;

}

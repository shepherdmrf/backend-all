package shu.xai.材料.service;

import com.alibaba.fastjson.JSONObject;

public interface PlatformService {
    JSONObject GetSign(String userId, String roleId);
    JSONObject SolveFeatureTable(String userId, String roleId, int page, int pageSize, String value1, String value2);
    JSONObject SolveKnowledgeKernel(String userId, String roleId, String value);
    JSONObject SolvePositiveKernel(String userId, String roleId, String value);
}


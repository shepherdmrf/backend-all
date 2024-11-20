package shu.xai.材料.service;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface PlatformService {
    JSONObject GetSign(String userId, String roleId);
    JSONObject SolveFeatureTable(String userId, String roleId, int page, int pageSize, String value1, String value2);
    JSONObject SolveKnowledgeKernel(String userId, String roleId, String value);
    JSONObject SolvePositiveKernel(String userId, String roleId, String value);
    JSONObject trainingresolve(String userId, String roleId, int pattern, List<List<Integer>> KnowledgeKernels);
}


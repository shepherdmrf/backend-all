package shu.xai.材料.service;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface PlatformService {
    JSONObject GetSign(String userId, String roleId);
    void SolveFeatureTable(String userId, String roleId,int repeat,int iteration);
    JSONObject SearchFeatureTable(String userId, String roleId, int page, int pageSize, String value1);
    void SolvePositiveKernel(String userId, String roleId);
    JSONObject GetKnowlegekernels(String userId, String roleId, List<List<Integer>> KnowledgeKernels);
    void Solvecluster();
    void SolvesimpleDecision(String userId, String roleId);
    JSONObject Search(String userId, String roleId,String value);
    void Solvekernel(String userId,String roleId);
    JSONObject DrawMlrPicture(String userId,String roleId);
}


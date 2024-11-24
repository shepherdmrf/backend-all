package shu.xai.材料.service.impl;

import com.alibaba.fastjson.JSONArray;

import com.alibaba.fastjson.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.sys.enums.RoleCodeEnums;
import shu.xai.材料.service.PlatformService;
import shu.xai.材料.service.CallPython.CallPythonScript;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class PlatformServiceImpl implements PlatformService {

    @Resource(name = "jdbcTemplatefeaturetable")
    private JdbcTemplate jdbcTemplatefeature;

    public JSONObject GetSign(String userId, String roleId) {
        JSONArray result = new JSONArray();
        JSONObject response = new JSONObject();
        if (roleId.equals(RoleCodeEnums.KAIFA.getCode()) || roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
            try {
                String sql = "SELECT name FROM sign_table";
                List<Map<String, Object>> searchResult;
                searchResult = jdbcTemplatefeature.queryForList(sql);
                for (Map<String, Object> row : searchResult) {
                    result.add(new JSONObject(row));
                }
                response.put("result", result);
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public JSONObject SolveFeatureTable(String userId, String roleId, int page, int pageSize, String value1, String value2) {
        JSONArray result = new JSONArray();
        JSONObject response = new JSONObject();
        int start = (page - 1) * pageSize;
        int totalRecords;

        if (roleId.equals(RoleCodeEnums.KAIFA.getCode()) || roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
            try {
                List<Map<String, Object>> searchResult;
                String sql;

                if (value2 == null || value2.isEmpty()) {
                    // 确保 SQL 语句中的空格
                    sql = "SELECT * FROM " + value1 + " LIMIT ? OFFSET ?";
                    totalRecords = jdbcTemplatefeature.queryForObject("SELECT COUNT(*) FROM " + value1, Integer.class);
                    searchResult = jdbcTemplatefeature.queryForList(sql, pageSize, start);
                } else {
                    sql = "SELECT COUNT(*) FROM " + value1 + " WHERE tag = ?";
                    totalRecords = jdbcTemplatefeature.queryForObject(sql, new Object[]{value2}, Integer.class);
                    sql = "SELECT * FROM " + value1 + " WHERE tag = ? LIMIT ? OFFSET ?";
                    ;
                    searchResult = jdbcTemplatefeature.queryForList(sql, value2, pageSize, start);
                }

                for (Map<String, Object> row : searchResult) {
                    result.add(new JSONObject(row));
                }
                response.put("list", result);
                response.put("total", totalRecords);
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    public JSONObject SolveKnowledgeKernel(String userId, String roleId, String value) {
        JSONArray result = new JSONArray();
        JSONObject response = new JSONObject();
        int totalRecords;
        try {
            List<Map<String, Object>> searchResult;
            String sql;
            sql = "SELECT * FROM " + value;
            searchResult = jdbcTemplatefeature.queryForList(sql);
            totalRecords=jdbcTemplatefeature.queryForObject("SELECT COUNT(*) FROM " + value, Integer.class);
            for (Map<String, Object> row : searchResult) {
                result.add(new JSONObject(row));
            }
            response.put("list", result);
            response.put("total", totalRecords);
        }catch (DataAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public JSONObject SolvePositiveKernel(String userId, String roleId, String value) {
        JSONArray result = new JSONArray();
        JSONObject response = new JSONObject();
        int totalRecords;
        try {
            List<Map<String, Object>> searchResult;
            String sql;
            sql = "SELECT * FROM " + value;
            searchResult = jdbcTemplatefeature.queryForList(sql);
            totalRecords=jdbcTemplatefeature.queryForObject("SELECT COUNT(*) FROM " + value, Integer.class);
            for (Map<String, Object> row : searchResult) {
                result.add(new JSONObject(row));
            }
            response.put("list", result);
            response.put("total", totalRecords);
        }catch (DataAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public JSONObject GetKnowlegekernels(String userId, String roleId,List<List<Integer>> KnowledgeKernels) {
        CallPythonScript.CallKnowlegeKernal(KnowledgeKernels.toString());
        return null;
    }
}


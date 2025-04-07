package shu.xai.材料.service.impl;

import com.alibaba.fastjson.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.classmate.Annotations;
import com.sun.org.apache.bcel.internal.generic.INEG;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.sys.enums.RoleCodeEnums;
import shu.xai.材料.ExcelSolve.ExcelSqlSolve;
import shu.xai.材料.page.treeNode;
import shu.xai.材料.service.CallPython.CallPythonScript;
import shu.xai.材料.service.PlatformService;
import javax.annotation.Resource;
import javax.validation.constraints.Null;
import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.File;

@Service
public class PlatformServiceImpl implements PlatformService {

    @Resource(name = "jdbcTemplatefeaturetable")
    private JdbcTemplate jdbcTemplatefeature;

    @Resource(name = "jdbcTemplatedataTable")
    private JdbcTemplate jdbcTemplatedata;

    @Resource(name = "jdbcTemplateRule")
    private JdbcTemplate jdbcTemplateRule;


    @Autowired
    private ExcelSqlSolve excelSqlSolve;

    public JSONObject GetSign(String userId, String roleId) {
        excelSqlSolve.insertSignToDatabase();
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

    public void SolveFeatureTable(String userId, String roleId, int repeat, int iteration,String value,String radio) {
        List<String> arg1 = Arrays.asList(String.valueOf(1));
        String work1 = "./src/main/resources/python/MultifacetedModeling/DataPreProcessing";
        String script1 = "./split_data.py";
        CallPythonScript.call(script1, work1, arg1);
        List<String> arg2 = Arrays.asList(String.valueOf(1), String.valueOf(repeat), String.valueOf(iteration));
        String work2 = "./src/main/resources/python/MultifacetedModeling/CodesForValidation";
        String script2 = "./DKncorFSonTrain.py";
        CallPythonScript.call(script2, work2, arg2);
        List<String> models = Arrays.asList("GPR", "KNN", "MLR", "SVR");
        // 清除数据库表并插入数据
        for (int i = 0; i < models.size(); i++) {

            String tablename = models.get(i) + "_feature_table";
            String Sheetname = "result0";
            try {
                excelSqlSolve.clearTable(tablename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String exceladdress = "./src/main/resources/python/MultifacetedModeling/Results/OnTrain/DKNCOR" + "/" + models.get(i) + ".xlsx";
            String Sql = "INSERT INTO `" + tablename + "` (`features`, `Length`, `CV RMSE`, `Score`, `Test RMSE`, `Test R2` , `Time`) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try {
                excelSqlSolve.insertExcelDataToSQL(exceladdress, Sql, Sheetname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject SearchFeatureTable(String userId, String roleId, int page, int pageSize, String value1) {
        JSONArray result = new JSONArray();
        JSONObject response = new JSONObject();
        int start = (page - 1) * pageSize;
        int totalRecords;
        if (roleId.equals(RoleCodeEnums.KAIFA.getCode()) || roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
            try {
                List<Map<String, Object>> searchResult;
                String sql;
                // 确保 SQL 语句中的空格
                sql = "SELECT * FROM " + value1 + " LIMIT ? OFFSET ?";
                searchResult = jdbcTemplatefeature.queryForList(sql, pageSize, start);
                totalRecords = jdbcTemplatefeature.queryForObject("SELECT COUNT(*) FROM " + value1, Integer.class);
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
    public void SolvePositiveKernel(String userId, String roleId) {
        String work = "./src/main/resources/python/MultifacetedModeling/KernelCal";
        String script1 = "./step6_cal_best_class.py";
        String script2 = "./step7_cal_kernel_freq.py";
        CallPythonScript.call(script1, work);
        CallPythonScript.call(script2, work);
        String inserthead = "insert into ";
        String insertpart = " (`positive_kernel`,`negative_kernel`) VALUES (?,?)";
        List<String> Models = Arrays.asList("GPR", "KNN", "MLR", "SVR");

        List<String> models = Arrays.asList("gpr_positive_kernel", "knn_positive_kernel", "mlr_positive_kernel", "svr_positive_kernel");
        for (int i = 0; i < models.size(); i++) {
            try {
                excelSqlSolve.clearTable(models.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String insertSql = inserthead + models.get(i) + insertpart;
            System.out.println(insertSql);
            String exceladdress = "./src/main/resources/python/MultifacetedModeling/DataOutput/" + Models.get(i) + "/final_kernels.xlsx";
            try {
                excelSqlSolve.insertExcelDataToSQL(exceladdress, insertSql, "kernels");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject GetKnowlegekernels(String userId, String roleId, List<List<Integer>> KnowledgeKernels) {
        // 调用 Python 脚本
        List<String> arg = Arrays.asList("10");
        String work1 = "./src/main/resources/python/MultifacetedModeling/DataPreProcessing";
        String script1 = "./split_data.py";
        CallPythonScript.call(script1, work1, arg);
        CallPythonScript.CallKnowlegeKernal(KnowledgeKernels.toString());
        System.out.println(KnowledgeKernels.size());

        // 模型名称列表
        List<String> models = Arrays.asList("GPR", "KNN", "MLR", "SVR");

        // 清除数据库表并插入数据
        for (int i = 0; i < models.size(); i++) {
            String tablename = models.get(i) + "_knowledgekernels_analysis";
            String Sheetname = models.get(i) + "-10";
            try {
                excelSqlSolve.clearTable(tablename);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int j = 0; j < KnowledgeKernels.size(); j++) {
                String exceladdress = "./src/main/resources/python/MultifacetedModeling/Results/OnTrain/KnowledgeKernel/" + models.get(i) + "/" + String.valueOf(j) + ".xlsx";
                String Sql = "INSERT INTO `" + tablename + "` (`CV RMSE`, `Test RMSE`, `Test R2`, `group`) VALUES (?, ?, ?, ?)";

                try {
                    excelSqlSolve.insertExcelDataToSQL(exceladdress, Sql, Sheetname, j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 构建返回的 JSON 响应
        JSONObject response = new JSONObject();
        JSONObject ModelAnalysis = new JSONObject();  // 使用 JSONObject 来存储模型和对应的分析数据

        // 获取数据库中的分析数据
        for (int i = 0; i < models.size(); i++) {
            String tablename = models.get(i) + "_knowledgekernels_analysis";
            JSONArray list = new JSONArray();

            for (int j = 0; j < KnowledgeKernels.size(); j++) {
                try {
                    JSONArray result = new JSONArray();
                    List<Map<String, Object>> searchResult;
                    String sql = "SELECT `CV RMSE`, `Test RMSE`, `Test R2`, `group` FROM " + tablename + " WHERE `group` = ?";
                    searchResult = jdbcTemplatefeature.queryForList(sql, j);

                    for (Map<String, Object> row : searchResult) {
                        result.add(new JSONObject(row));
                    }
                    list.add(result);
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }
            }

            // 将每个模型的数据存入 ModelAnalysis 对象
            ModelAnalysis.put(models.get(i), list);  // 用模型名称作为键，将分析数据作为值
        }

        // 遍历 ModelAnalysis，按组找到最小 CV RMSE 的值
        JSONObject result = new JSONObject();
        for (int i = 0; i < models.size(); i++) {
            JSONArray temp = new JSONArray();
            JSONArray list = ModelAnalysis.getJSONArray(models.get(i));  // 获取模型的所有分析数据

            // 每组数据的最小 CV RMSE 处理
            for (int k = 0; k < list.getJSONArray(0).size(); k++) {
                JSONObject min = list.getJSONArray(0).getJSONObject(k); // 默认第一个作为最小值
                double minValue = min.getDouble("CV RMSE"); // 根据 CV RMSE 比较

                // 遍历其他组，找到最小的 CV RMSE
                for (int j = 1; j < list.size(); j++) {
                    JSONObject current = list.getJSONArray(j).getJSONObject(k);
                    double currentValue = current.getDouble("CV RMSE");

                    if (currentValue < minValue) {
                        min = current;
                        minValue = currentValue;
                    }
                }
                JSONObject m = new JSONObject(min);
                temp.add(m);  // 将最小值加入到当前组的结果中
            }
            result.put(models.get(i), temp);  // 将每个组的最小值集合加入到最终结果中
        }

        // 将结果插入数据库
        for (int i = 0; i < result.size(); i++) {
            String tablename = models.get(i) + "_knowlegekernel";
            try {
                excelSqlSolve.clearTable(tablename);
                JSONArray list = result.getJSONArray(models.get(i));

                for (int j = 0; j < list.size(); j++) {
                    JSONObject object = list.getJSONObject(j);
                    int group = object.getInteger("group");

                    // 根据 group 获取对应的知识核选择项
                    String choice = String.valueOf(KnowledgeKernels.get(group));
                    double CVRSME = object.getDouble("CV RMSE");
                    double TestRMSE = object.getDouble("Test RMSE");
                    double Test_R2 = object.getDouble("Test R2");

                    String sql = "INSERT INTO `" + tablename + "` (`feature`, `CV RMSE`, `Test RMSE`, `Test R2`) VALUES (?, ?, ?, ?)";
                    jdbcTemplatefeature.update(sql, choice, CVRSME, TestRMSE, Test_R2);
                }

                System.out.println("数据已成功插入到数据库！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 返回响应
        response.put("ModelAnalysis", ModelAnalysis);  // 将 ModelAnalysis 放入响应中
        response.put("Result", result);  // 将结果放入响应中
        return response;
    }

    @Override
    public void Solvecluster() {
        String script = "./step1_clustering.py";
        String work = "./src/main/resources/python/MultifacetedModeling/KernelCal";
        CallPythonScript.call(script, work);
        List<String> models = Arrays.asList("GPR", "KNN", "MLR", "SVR");
        // 清除数据库表并插入数据
        for (int i = 0; i < models.size(); i++) {

            String tablename = models.get(i) + "_feature_table";
            String Sheetname = "result0";
            try {
                excelSqlSolve.clearTable(tablename);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String exceladdress = "./src/main/resources/python/MultifacetedModeling/Results/OnTrain/DKNCOR" + "/" + models.get(i) + ".xlsx";
            String Sql = "INSERT INTO `" + tablename + "` (`features`, `Length`, `CV RMSE`, `Score`, `Test RMSE`, `Test R2` , `Time` , `d_class` ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                excelSqlSolve.insertExcelDataToSQL(exceladdress, Sql, Sheetname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void SolvesimpleDecision(String userId, String roleId) {
        String work = "./src/main/resources/python/MultifacetedModeling/KernelCal";
        String script0 = "./step2_count.py";
        String script1 = "./step3_threshold_analysis.py";
        String script2 = "./step4_decision_table_construct.py";
        CallPythonScript.call(script0, work);
        CallPythonScript.call(script1, work);
        CallPythonScript.call(script2, work);
        String insertpart = excelSqlSolve.creatSqledecision_table();
        String inserthead = "insert into ";
        List<String> Models = Arrays.asList("GPR", "KNN", "MLR", "SVR");

        List<String> models = Arrays.asList("gpr_decision_table", "knn_decision_table", "mlr_decision_table", "svr_decision_table");
        for (int i = 0; i < models.size(); i++) {
            try {
                excelSqlSolve.clearTable(models.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String insertSql = inserthead + models.get(i) + insertpart;
            System.out.println(insertSql);
            String exceladdress = "./src/main/resources/python/MultifacetedModeling/DataOutput/" + Models.get(i) + "/decision_table.xlsx";
            try {
                excelSqlSolve.insertExcelDataToSQL(exceladdress, insertSql, "result0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject Search(String userId, String roleId, String value) {
        JSONArray result = new JSONArray();
        JSONObject response = new JSONObject();
        try {
            List<Map<String, Object>> searchResult;
            String sql;
            sql = "SELECT * FROM " + value;
            searchResult = jdbcTemplatefeature.queryForList(sql);
            for (Map<String, Object> row : searchResult) {
                result.add(new JSONObject(row));
            }
            response.put("list", result);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void Solvekernel(String userId, String roleId) {
        String work = "./src/main/resources/python/MultifacetedModeling/KernelCal";
        String script = "./step5_kernel_cal.py";
        CallPythonScript.call(script, work);
        String inserthead = "insert into ";
        String insertPart = " (`kernels`) VALUES (?)";
        List<String> Models = Arrays.asList("GPR", "KNN", "MLR", "SVR");

        List<String> models = Arrays.asList("gpr_kernel", "knn_kernel", "mlr_kernel", "svr_kernel");
        for (int i = 0; i < models.size(); i++) {
            try {
                excelSqlSolve.clearTable(models.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String insertSql = inserthead + models.get(i) + insertPart;
            System.out.println(insertSql);
            String exceladdress = "./src/main/resources/python/MultifacetedModeling/DataOutput/" + Models.get(i) + "/kernels.xlsx";
            try {
                excelSqlSolve.insertExcelDataToSQL(exceladdress, insertSql, "data");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject DrawMlrPicture(String userId, String roleId) throws Exception {
        JSONArray result1 = new JSONArray();
        JSONArray result2 = new JSONArray();
        JSONObject response = new JSONObject();
        if (roleId.equals(RoleCodeEnums.KAIFA.getCode()) || roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
            String s = "";
            try {
                List<Map<String, Object>> searchResult;
                String sql;

                // 确保 SQL 语句中的空格
                sql = "SELECT Features FROM bestmlr";
                searchResult = jdbcTemplatedata.queryForList(sql);
                s = String.valueOf(searchResult.get(0).get("Features"));
                System.out.println(s);

            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            String work = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/MLR";
            String script = "./training.py";
            List<String> list = new ArrayList<>();
            list.add(s);
            CallPythonScript.call(script, work, list);
            script = "./correlation_cal.py";
            CallPythonScript.call(script, work, list);
            List<Double> Importance = new ArrayList<>();
            List<Double> PCC=new ArrayList<>();
            try {
                excelSqlSolve.RuleClear("MLR1");
                excelSqlSolve.RuleClear("MLR2");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
               Importance=excelSqlSolve.readFirstColumn("./src/main/resources/python/MultifacetedModeling/RuleExtraction/MLR/training_result.xlsx","data-3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try
            {
                PCC=excelSqlSolve.readLastRow("src/main/resources/python/MultifacetedModeling/RuleExtraction/MLR/correlation_coef.xlsx","all_data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Integer> id = Arrays.stream(s.replaceAll("[\\[\\]]", "").split(", "))
                    .map(Integer::parseInt)  // 使用 map 而不是 mapToInt
                    .collect(Collectors.toList());
            String sql="SELECT * FROM sign_table";
            List<Map<String, Object>> Features;
            Features=jdbcTemplatefeature.queryForList(sql);
            List<String>FeaturesName = new LinkedList<>();
            for(int i=0;i<Features.size();i++)
            {
                FeaturesName.add(Features.get(i).get("name").toString());
            }

            sql = "INSERT INTO `MLR1` (`No.`, `Name`, `Importance`, `PCC`) VALUES (?, ?, ?, ?)";
            for (int i = 0; i<id.size();i++)
            {
                jdbcTemplateRule.update(sql,id.get(i),FeaturesName.get(id.get(i)),Importance.get(i),PCC.get(i));

            }
            sql = "INSERT INTO `MLR2` (`No.`, `Name`, `Importance`, `PCC`) VALUES (?, ?, ?, ?)";
            for (int i = 0; i<id.size();i++)
            {
                if (PCC.get(i)*Importance.get(i)>=0) {
                    jdbcTemplateRule.update(sql, id.get(i), FeaturesName.get(id.get(i)), Importance.get(i), PCC.get(i));
                }
            }
            try {
                List<Map<String, Object>> searchResult;
                // 确保 SQL 语句中的空格
                sql = "SELECT Name , Importance , PCC FROM MLR1";
                searchResult = jdbcTemplateRule.queryForList(sql);
                for (Map<String, Object> row : searchResult) {
                    result1.add(new JSONObject(row));
                }
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            response.put("list1", result1);

            try {
                List<Map<String, Object>> searchResult;
                // 确保 SQL 语句中的空格
                sql = "SELECT Name , Importance , PCC FROM MLR2";
                searchResult = jdbcTemplateRule.queryForList(sql);
                for (Map<String, Object> row : searchResult) {
                    result2.add(new JSONObject(row));
                }
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            response.put("list2", result2);
        }
        System.out.println(response);
        return response;
    }

    public boolean search(treeNode root, Integer target) {
        Queue<treeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            treeNode temp = queue.poll();
            if (temp.isLeaf() && target.equals(temp.getValue()))
                return true;
            else {
                if (temp.getLeft() != null)
                    queue.add(temp.getLeft());
                if (temp.getRight() != null)
                    queue.add(temp.getRight());
            }
        }
        return false;
    }

    @Override
    public JSONObject DrawKnnPicture(String userId, String roleId) {
        String excelFile = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/data_cluster.xlsx";
        String Sheetname = "data";
        String sql = "INSERT INTO `knn_data` (`formula`) VALUES (?)";
        List<Integer> columnsToRead = Arrays.asList(2);
        try {
            excelSqlSolve.RuleClear("knn_data");
            excelSqlSolve.RuleinsertExcelDataToSQL(excelFile, sql, Sheetname, columnsToRead, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> formula;
        sql = "SELECT formula FROM knn_data";
        formula = jdbcTemplateRule.queryForList(sql);
        ArrayList<String> formula_name = new ArrayList<>();
        List<Map<String, Object>> best;
        sql = "SELECT * FROM bestknn";
        List<String> bestfeatures=new ArrayList<>();
        best=jdbcTemplatedata.queryForList(sql);
        bestfeatures.add(best.get(0).get("Features").toString());
        for (Map<String, Object> row : formula) {
            formula_name.add(String.valueOf(row.get("formula")));
        }
        String work = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN";
        String script = "./visualization.py";
        CallPythonScript.call(script, work,bestfeatures);

        excelFile = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/clustering_data_tree.xlsx";
        Sheetname = "Hierarchical Clustering Tree";
        sql = "INSERT INTO `knn_tree` (`element1`,`element2`,`distance`) VALUES (?,?,?)";
        try {
            excelSqlSolve.RuleClear("knn_tree");
            excelSqlSolve.RuleinsertExcelDataToSQL(excelFile, sql, Sheetname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Integer> nodes = new HashSet<>();
        List<Map<String, Object>> relation;
        ArrayList<treeNode> tree = new ArrayList<>();
        ArrayList<JSONObject> treeNodeRelation = new ArrayList<>();
        ;
        sql = "SELECT DISTINCT element1, element2, distance FROM knn_tree";
        relation = jdbcTemplateRule.queryForList(sql);

        for (Map<String, Object> row : relation) {
            treeNodeRelation.add(new JSONObject(row));
        }
        try {
            for (int i = 0; i < treeNodeRelation.size(); i++) {

                Integer element1 = (Integer) treeNodeRelation.get(i).get("element1");
                Integer element2 = (Integer) treeNodeRelation.get(i).get("element2");
                boolean left = nodes.contains(element1);
                boolean right = nodes.contains(element2);
                if (left && right) {
                    int j = 0;
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element1)) {
                            break;
                        }
                    }
                    treeNode tree_left = tree.get(j);
                    tree.remove(j);
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element2)) {
                            break;
                        }
                    }
                    treeNode tree_right = tree.get(j);
                    tree.remove(j);
                    tree.add(new treeNode(tree_left, tree_right, false));
                } else if (!left && !right) {
                    nodes.add(element1);
                    nodes.add(element2);
                    treeNode tree_left = new treeNode(null, null, true, element1);
                    treeNode tree_right = new treeNode(null, null, true, element2);
                    tree.add(new treeNode(tree_left, tree_right, false));
                } else if (left) {
                    int j = 0;
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element1)) {
                            break;
                        }
                    }
                    treeNode tree_left = tree.get(j);
                    tree.remove(j);
                    treeNode tree_right = new treeNode(null, null, true, element2);
                    nodes.add(element2);
                    tree.add(new treeNode(tree_left, tree_right, false));
                } else if (right) {
                    int j = 0;
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element2)) {
                            break;
                        }
                    }
                    treeNode tree_left = new treeNode(null, null, true, element1);
                    treeNode tree_right = tree.get(j);
                    tree.remove(j);
                    nodes.add(element1);
                    tree.add(new treeNode(tree_left, tree_right, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();
        JSONArray treeList = new JSONArray();
        treeNode root = tree.get(tree.size() - 1);
        Queue<treeNode> queue = new LinkedList<>();
        Queue<Integer> parentQueue = new LinkedList<>();
        queue.add(root);
        parentQueue.add(-1);
        Integer id = 1;
        while (!queue.isEmpty()) {
            treeNode temp = queue.poll();
            Integer parentId = parentQueue.poll();
            JSONObject node = new JSONObject();
            node.put("id", id);
            node.put("parentId", parentId);

            if (temp.isLeaf() == true) {
                node.put("formula", formula_name.get(temp.getValue()));
                node.put("value", temp.getValue());
            } else
                node.put("value", -1);
            treeList.add(node);
            if (temp.getLeft() != null) {
                queue.add(temp.getLeft());
                parentQueue.add(id);
            }
            if (temp.getRight() != null) {
                queue.add(temp.getRight());
                parentQueue.add(id);
            }
            id++;
        }
        result.put("list", treeList);
        return result;
    }

    private void collectLeaves(treeNode node, List<Integer> leaves) {
        if (node == null) return;

        if (node.isLeaf()) {
            leaves.add(node.getValue());
        } else {
            collectLeaves(node.getLeft(), leaves);
            collectLeaves(node.getRight(), leaves);
        }
    }

    private void processTreeLayers(treeNode root, JSONObject result) {
        if (root == null) return;

        Queue<treeNode> queue = new LinkedList<>();
        queue.add(root);

        int level = 1;
        while (!queue.isEmpty() && level <= 3) {
            int levelSize = queue.size();
            int nodeNumber = 1;

            for (int i = 0; i < levelSize; i++) {
                treeNode node = queue.poll();

                // 收集当前节点的所有叶子节点
                List<Integer> leaves = new ArrayList<>();
                collectLeaves(node, leaves);

                // 生成层级-节点键名
                String key = level + "-" + nodeNumber;
                result.put(key, leaves);

                // 将子节点加入队列（仅处理到第三层）
                if (level < 3) {
                    if (node.getLeft() != null) queue.add(node.getLeft());
                    if (node.getRight() != null) queue.add(node.getRight());
                }

                nodeNumber++;
            }
            level++;
        }
    }

    @Override
    public JSONObject Analyrizeknn(String userId, String roleId) {
        String excelFile = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/data_cluster.xlsx";
        String Sheetname = "data";
        String sql = "INSERT INTO `knn_data` (`formula`) VALUES (?)";
        List<Integer> columnsToRead = Arrays.asList(2);
        try {
            excelSqlSolve.RuleClear("knn_data");
            excelSqlSolve.RuleinsertExcelDataToSQL(excelFile, sql, Sheetname, columnsToRead, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> formula;
        sql = "SELECT formula FROM knn_data";
        formula = jdbcTemplateRule.queryForList(sql);
        ArrayList<String> formula_name = new ArrayList<>();
        ;
        for (Map<String, Object> row : formula) {
            formula_name.add(String.valueOf(row.get("formula")));
        }
        String work = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN";
        String script = "./visualization.py";
        CallPythonScript.call(script, work);

        excelFile = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/clustering_data_tree.xlsx";
        Sheetname = "Hierarchical Clustering Tree";
        sql = "INSERT INTO `knn_tree` (`element1`,`element2`,`distance`) VALUES (?,?,?)";
        try {
            excelSqlSolve.RuleClear("knn_tree");
            excelSqlSolve.RuleinsertExcelDataToSQL(excelFile, sql, Sheetname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Integer> nodes = new HashSet<>();
        List<Map<String, Object>> relation;
        ArrayList<treeNode> tree = new ArrayList<>();
        ArrayList<JSONObject> treeNodeRelation = new ArrayList<>();
        ;
        sql = "SELECT DISTINCT element1, element2, distance FROM knn_tree";
        relation = jdbcTemplateRule.queryForList(sql);

        for (Map<String, Object> row : relation) {
            treeNodeRelation.add(new JSONObject(row));
        }
        try {
            for (int i = 0; i < treeNodeRelation.size(); i++) {

                Integer element1 = (Integer) treeNodeRelation.get(i).get("element1");
                Integer element2 = (Integer) treeNodeRelation.get(i).get("element2");
                boolean left = nodes.contains(element1);
                boolean right = nodes.contains(element2);
                if (left && right) {
                    int j = 0;
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element1)) {
                            break;
                        }
                    }
                    treeNode tree_left = tree.get(j);
                    tree.remove(j);
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element2)) {
                            break;
                        }
                    }
                    treeNode tree_right = tree.get(j);
                    tree.remove(j);
                    tree.add(new treeNode(tree_left, tree_right, false));
                } else if (!left && !right) {
                    nodes.add(element1);
                    nodes.add(element2);
                    treeNode tree_left = new treeNode(null, null, true, element1);
                    treeNode tree_right = new treeNode(null, null, true, element2);
                    tree.add(new treeNode(tree_left, tree_right, false));
                } else if (left) {
                    int j = 0;
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element1)) {
                            break;
                        }
                    }
                    treeNode tree_left = tree.get(j);
                    tree.remove(j);
                    treeNode tree_right = new treeNode(null, null, true, element2);
                    nodes.add(element2);
                    tree.add(new treeNode(tree_left, tree_right, false));
                } else if (right) {
                    int j = 0;
                    for (j = 0; j < tree.size(); j++) {
                        if (search(tree.get(j), element2)) {
                            break;
                        }
                    }
                    treeNode tree_left = new treeNode(null, null, true, element1);
                    treeNode tree_right = tree.get(j);
                    tree.remove(j);
                    nodes.add(element1);
                    tree.add(new treeNode(tree_left, tree_right, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!tree.isEmpty()) {
            treeNode root = tree.get(tree.size() - 1);
            JSONObject result = new JSONObject();

            // 使用广度优先遍历处理前三层
            processTreeLayers(root, result);

            return result;
        } else {
            return null;
        }
    }

    @Override
    public void ServerExcelTable(JSONObject list) {
        try {
            for (String key : list.keySet()) {
                String name = key;
                JSONArray value=list.getJSONArray(name);
                List<Integer> arrayList = new ArrayList<>();
                for (int i = 0; i < value.size(); i++) {
                    String numStr = value.getString(i);
                    arrayList.add(Integer.parseInt(numStr));
                }
                String srcFile = "./src/main/resources/python/MultifacetedModeling/DataInput/uploadedFile.xlsx";
                String outFile = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/TreeCluster/" + name + ".xlsx";
                String srcSheet = "data";
                String outName = "data";
                excelSqlSolve.ServerExcel(srcFile, outFile, srcSheet, outName,arrayList,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray convertToDescription(List<Map<String, Object>> rules) {
        JSONArray result = new JSONArray();
        if (rules == null || rules.isEmpty()) {
            return result;
        }

        // 1. 按category分组
        Map<String, List<Map<String, Object>>> categoryMap = new HashMap<>();
        for (Map<String, Object> rule : rules) {
            String category = rule.get("category") != null ? rule.get("category").toString() : "";
            categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(rule);
        }

        // 2. 处理每个category
        for (Map.Entry<String, List<Map<String, Object>>> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            List<Map<String, Object>> ruleList = entry.getValue();

            switch (category) {
                case "通道瓶颈尺寸参数":
                    JSONObject channelDesc = new JSONObject();
                    channelDesc.put("description", "通道尺寸");

                    int positive = 0, negative = 0;
                    for (Map<String, Object> rule : ruleList) {
                        double importance = 0;
                        try {
                            importance = Double.parseDouble(rule.get("Importance").toString());
                        } catch (Exception e) {
                            continue;
                        }

                        if (importance > 0) positive++;
                        else if (importance < 0) negative++;
                    }

                    if (positive > negative) {
                        channelDesc.put("direction", "↑ → 势垒 ↑");
                        channelDesc.put("trend", "positive");
                    } else {
                        channelDesc.put("direction", "↑ → 势垒 ↓");
                        channelDesc.put("trend", "negative");
                    }
                    result.add(channelDesc);
                    break;

                case "通用结构参数":
                    boolean hasCellSize = false;
                    boolean hasPos = false, hasNeg = false;

                    for (Map<String, Object> rule : ruleList) {
                        String descriptor = rule.get("descriptor_name") != null ?
                                rule.get("descriptor_name").toString() : "";
                        double importance = 0;
                        try {
                            importance = Double.parseDouble(rule.get("Importance").toString());
                        } catch (Exception e) {
                            continue;
                        }

                        // 检查晶胞参数
                        if (descriptor.equals("a") || descriptor.equals("c") || descriptor.equals("V")) {
                            hasCellSize = true;
                            if (importance > 0) hasPos = true;
                            else if (importance < 0) hasNeg = true;
                            continue;
                        }

                        // 处理普通参数
                        JSONObject desc = new JSONObject();
                        desc.put("description", rule.get("physical_meaning") != null ?
                                rule.get("physical_meaning").toString() : "");
                        if (importance < 0) {
                            desc.put("direction", "↑ → 势垒 ↓");
                            desc.put("trend", "negative");
                        } else {
                            desc.put("direction", "↑ → 势垒 ↑");
                            desc.put("trend", "positive");
                        }
                        result.add(desc);
                    }

                    // 处理晶胞尺寸
                    if (hasCellSize) {
                        JSONObject cellDesc = new JSONObject();
                        cellDesc.put("description", "晶胞尺寸");

                        if (hasNeg && !hasPos) {
                            cellDesc.put("direction", "↑ → 势垒 ↓");
                            cellDesc.put("trend", "negative");
                            result.add(cellDesc);
                        } else if (!hasNeg && hasPos) {
                            cellDesc.put("direction", "↑ → 势垒 ↑");
                            cellDesc.put("trend", "positive");
                            result.add(cellDesc);
                        }
                        // 可选：处理混合情况
                    }
                    break;

                case "热力学与熵参数":
                    for (Map<String, Object> rule : ruleList) {
                        double importance = 0;
                        try {
                            importance = Double.parseDouble(rule.get("Importance").toString());
                        } catch (Exception e) {
                            continue;
                        }
                        if (rule.get("descriptor_name").toString().equals("T"))
                        {
                            JSONObject desc = new JSONObject();
                            desc.put("description", "温度");
                            if (importance < 0) {
                                desc.put("direction", "↑ → 势垒 ↓");
                                desc.put("trend", "negative");
                            } else {
                                desc.put("direction", "↑ → 势垒 ↑");
                                desc.put("trend", "positive");
                            }
                            result.add(desc);
                        }
                        else{
                            JSONObject desc = new JSONObject();
                            String description = rule.get("descriptor_name")+"构型熵";
                            desc.put("description", description);
                            if (importance < 0) {
                                desc.put("direction", "↑ → 势垒 ↓");
                                desc.put("trend", "negative");
                            } else {
                                desc.put("direction", "↑ → 势垒 ↑");
                                desc.put("trend", "positive");
                            }
                            result.add(desc);
                        }
                    }
                    break;

                case "M元素相关特性":
                case "X元素相关特性":
                    JSONObject mixDesc = new JSONObject();
                    String elementType = category.equals("M元素相关特性") ? "M位" : "X位";
                    mixDesc.put("description", elementType + "元素混杂度");

                    int mixPos = 0, mixNeg = 0;
                    for (Map<String, Object> rule : ruleList) {
                        double importance = 0;
                        try {
                            importance = Double.parseDouble(rule.get("Importance").toString());
                        } catch (Exception e) {
                            continue;
                        }
                        String descriptor = rule.get("descriptor_name") != null ?
                                rule.get("descriptor_name").toString() : "";

                        // 精确匹配原子半径描述符
                        if (descriptor.contains("原子半径")) { // 根据实际描述符调整
                            JSONObject radiusDesc = new JSONObject();
                            radiusDesc.put("description", elementType + "原子半径");
                            if (importance < 0) {
                                radiusDesc.put("direction", "↑ → 势垒 ↓");
                                radiusDesc.put("trend", "negative");
                            } else {
                                radiusDesc.put("direction", "↑ → 势垒 ↑");
                                radiusDesc.put("trend", "positive");
                            }
                            result.add(radiusDesc);
                            continue;
                        }

                        // 其他参数计入混杂度统计
                        if (importance > 0) mixPos++;
                        else if (importance < 0) mixNeg++;
                    }

                    if (mixPos > mixNeg) {
                        mixDesc.put("direction", "↑ → 势垒 ↑");
                        mixDesc.put("trend", "positive");
                    } else if (mixNeg > mixPos) {
                        mixDesc.put("direction", "↑ → 势垒 ↓");
                        mixDesc.put("trend", "negative");
                    } else if (mixPos + mixNeg > 0) {
                        mixDesc.put("direction", "↑ → 势垒 ↓");
                        mixDesc.put("trend", "negative");
                    }

                    if (mixPos + mixNeg > 0) {
                        result.add(mixDesc);
                    }
                    break;
            }
        }
        return result;
    }
    @Override
    public JSONObject MLRRelationAnalyze(String userId, String roleId) {
        List<Map<String, Object>>rules = new ArrayList<>();
        String sql="SELECT * FROM mlr2";
        List<Map<String, Object>> Features;
        Features = jdbcTemplateRule.queryForList(sql);
        sql="SELECT * FROM material_descriptors";
        List<Map<String, Object>> propriety;
        propriety=jdbcTemplateRule.queryForList(sql);
        for (Map<String, Object> feature : Features) {
            Integer id= Integer.valueOf(feature.get("No.").toString());
            Double importance=Double.valueOf(feature.get("Importance").toString());
            Map<String,Object>roll=propriety.get(id);
            roll.put("Importance",importance);
            rules.add(roll);
        }
        JSONArray list=convertToDescription(rules);
        JSONObject result=new JSONObject();
        result.put("list",list);
        return result;
    }
}


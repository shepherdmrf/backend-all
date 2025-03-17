package shu.xai.材料.service.impl;

import com.alibaba.fastjson.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.ibatis.jdbc.Null;
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
import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.*;
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

    public void SolveFeatureTable(String userId, String roleId,int repeat,int iteration) {
        List<String> arg1 = Arrays.asList(String.valueOf(1));
        String work1 = "./src/main/resources/python/MultifacetedModeling/DataPreProcessing";
        String script1 = "./split_data.py";
        CallPythonScript.call(script1, work1, arg1);
        List<String> arg2 = Arrays.asList(String.valueOf(1), String.valueOf(repeat), String.valueOf(iteration));
        String work2 = "./src/main/resources/python/MultifacetedModeling/CodesForValidation";
        String script2 = "./DKncorFSonTrain.py";
        CallPythonScript.call(script2,work2,arg2);
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
                totalRecords=jdbcTemplatefeature.queryForObject("SELECT COUNT(*) FROM " + value1, Integer.class);
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
        List<String> arg= Arrays.asList("10");
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
                JSONObject m=new JSONObject(min);
                temp.add(m);  // 将最小值加入到当前组的结果中
            }
            result.put(models.get(i),temp);  // 将每个组的最小值集合加入到最终结果中
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
        String script="./step1_clustering.py";
        String work="./src/main/resources/python/MultifacetedModeling/KernelCal";
        CallPythonScript.call(script,work);
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
        String work="./src/main/resources/python/MultifacetedModeling/KernelCal";
        String script0="./step2_count.py";
        String script1="./step3_threshold_analysis.py";
        String script2="./step4_decision_table_construct.py";
        CallPythonScript.call(script0,work);
        CallPythonScript.call(script1,work);
        CallPythonScript.call(script2,work);
        String insertpart= excelSqlSolve.creatSqledecision_table();
        String inserthead="insert into ";
        List<String> Models= Arrays.asList("GPR","KNN","MLR","SVR");

        List<String> models = Arrays.asList("gpr_decision_table", "knn_decision_table", "mlr_decision_table", "svr_decision_table");
        for (int i=0; i<models.size();i++) {
            try {
            excelSqlSolve.clearTable(models.get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String insertSql=inserthead+models.get(i)+insertpart;
        System.out.println(insertSql);
        String exceladdress="./src/main/resources/python/MultifacetedModeling/DataOutput/"+Models.get(i)+"/decision_table.xlsx";
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
        }catch (DataAccessException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void Solvekernel(String userId, String roleId) {
        String work="./src/main/resources/python/MultifacetedModeling/KernelCal";
        String script="./step5_kernel_cal.py";
        CallPythonScript.call(script,work);
        String inserthead="insert into ";
        String insertPart = " (`kernels`) VALUES (?)";
        List<String> Models= Arrays.asList("GPR","KNN","MLR","SVR");

        List<String> models = Arrays.asList("gpr_kernel", "knn_kernel", "mlr_kernel", "svr_kernel");
        for (int i=0; i<models.size();i++) {
            try {
                excelSqlSolve.clearTable(models.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String insertSql=inserthead+models.get(i)+insertPart;
            System.out.println(insertSql);
            String exceladdress="./src/main/resources/python/MultifacetedModeling/DataOutput/"+Models.get(i)+"/kernels.xlsx";
            try {
                excelSqlSolve.insertExcelDataToSQL(exceladdress, insertSql, "data");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject DrawMlrPicture(String userId, String roleId) {
        JSONArray result1 = new JSONArray();
        JSONArray result2 = new JSONArray();
        JSONObject response = new JSONObject();
        if (roleId.equals(RoleCodeEnums.KAIFA.getCode()) || roleId.equals(RoleCodeEnums.IN_MAMAGE.getCode())) {
            String s="";
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
            String work="./src/main/resources/python/MultifacetedModeling/RuleExtraction/MLR";
            String script="./training.py";
            List<String> list = new ArrayList<>();
            list.add(s);
            CallPythonScript.call(script,work,list);
            try{
                excelSqlSolve.RuleClear("MLR1");
                excelSqlSolve.RuleClear("MLR2");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sql="INSERT INTO `MLR1` (`No.`, `Name`, `Importance`, `PCC`) VALUES (?, ?, ?, ?)";
            String excelFile="./src/main/resources/python/MultifacetedModeling/RuleExtraction/MLR/training_result.xlsx";
            String sheetname="all";
            try{
                excelSqlSolve.RuleinsertExcelDataToSQL(excelFile,sql,sheetname);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sql="INSERT INTO `MLR2` (`No.`, `Name`, `Importance`, `PCC`) VALUES (?, ?, ?, ?)";
            excelFile="./src/main/resources/python/MultifacetedModeling/RuleExtraction/MLR/training_result.xlsx";
            sheetname="all_pos";
            try{
                excelSqlSolve.RuleinsertExcelDataToSQL(excelFile,sql,sheetname);
            } catch (Exception e) {
                e.printStackTrace();
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
            response.put("list1",result1);

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
            response.put("list2",result2);
        }
        System.out.println(response);
        return response;
    }

    public boolean search(treeNode root,Integer target)
    {
        Queue<treeNode> queue=new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()){
          treeNode temp=queue.poll();
            if (temp.isLeaf() && target.equals(temp.getValue()))
              return true;
          else {
              if (temp.getLeft()!=null)
                  queue.add(temp.getLeft());
              if(temp.getRight()!=null)
                  queue.add(temp.getRight());
          }
        }
        return false;
    }

    @Override
    public JSONObject DrawKnnPicture(String userId, String roleId) {
        String excelFile="./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/data_cluster.xlsx";
        String Sheetname="data";
        String sql="INSERT INTO `knn_data` (`formula`) VALUES (?)";
        List<Integer> columnsToRead = Arrays.asList(2);
        try{
            excelSqlSolve.RuleClear("knn_data");
            excelSqlSolve.RuleinsertExcelDataToSQL(excelFile,sql,Sheetname,columnsToRead,2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> formula;
        sql = "SELECT formula FROM knn_data";
        formula = jdbcTemplateRule.queryForList(sql);
        ArrayList<String> formula_name= new ArrayList<>();;
        for (Map<String, Object> row : formula) {
            formula_name.add(String.valueOf(row.get("formula")));
        }
        String work="./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN";
        String script="./visualization.py";
        CallPythonScript.call(script,work);

        excelFile="./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/clustering_data_tree.xlsx";
        Sheetname="Hierarchical Clustering Tree";
        sql="INSERT INTO `knn_tree` (`element1`,`element2`,`distance`) VALUES (?,?,?)";
        try{
            excelSqlSolve.RuleClear("knn_tree");
            excelSqlSolve.RuleinsertExcelDataToSQL(excelFile,sql,Sheetname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<Integer>nodes=new HashSet<>();
        List<Map<String, Object>> relation;
        ArrayList<treeNode> tree=new ArrayList<>();
        ArrayList<JSONObject> treeNodeRelation= new ArrayList<>();;
        sql = "SELECT DISTINCT element1, element2, distance FROM knn_tree";
        relation=jdbcTemplateRule.queryForList(sql);

        for (Map<String, Object> row : relation) {
            treeNodeRelation.add(new JSONObject(row));
        }
        try {
            for (int i=0;i<treeNodeRelation.size();i++)
            {

                Integer element1=(Integer) treeNodeRelation.get(i).get("element1");
                Integer element2=(Integer) treeNodeRelation.get(i).get("element2");
                boolean left=nodes.contains(element1);
                boolean right=nodes.contains(element2);
                if(left&&right)
                {
                    int j=0;
                    for (j=0;j<tree.size();j++){
                        if(search(tree.get(j),element1))
                        {
                            break;
                        }
                    }
                    treeNode tree_left=tree.get(j);
                    tree.remove(j);
                    for (j=0;j<tree.size();j++){
                        if(search(tree.get(j),element2))
                        {
                            break;
                        }
                    }
                    treeNode tree_right=tree.get(j);
                    tree.remove(j);
                    tree.add(new treeNode(tree_left,tree_right,false));
                }
                else if (!left&&!right)
                {
                    nodes.add(element1);
                    nodes.add(element2);
                    treeNode tree_left=new treeNode(null,null,true,element1);
                    treeNode tree_right=new treeNode(null,null,true,element2);
                    tree.add(new treeNode(tree_left,tree_right,false));
                }
                else if(left)
                {
                    int j=0;
                    for (j=0;j<tree.size();j++){
                        if(search(tree.get(j),element1))
                        {
                            break;
                        }
                    }
                    treeNode tree_left=tree.get(j);
                    tree.remove(j);
                    treeNode tree_right=new treeNode(null,null,true,element2);
                    nodes.add(element2);
                    tree.add(new treeNode(tree_left,tree_right,false));
                }
                else if(right)
                {
                    int j=0;
                    for (j=0;j<tree.size();j++){
                        if(search(tree.get(j),element2))
                        {
                            break;
                        }
                    }
                    treeNode tree_left=new treeNode(null,null,true,element1);
                    treeNode tree_right=tree.get(j);
                    tree.remove(j);
                    nodes.add(element1);
                    tree.add(new treeNode(tree_left,tree_right,false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result=new JSONObject();
        JSONArray treeList=new JSONArray();
        treeNode root=tree.get(tree.size()-1);
        Queue<treeNode>queue=new LinkedList<>();
        Queue<Integer>parentQueue=new LinkedList<>();
        queue.add(root);
        parentQueue.add(-1);
        Integer id=1;
        while(!queue.isEmpty())
        {
            treeNode temp=queue.poll();
            Integer parentId=parentQueue.poll();
            JSONObject node=new JSONObject();
            node.put("id",id);
            node.put("parentId",parentId);

            if(temp.isLeaf()==true) {
                node.put("formula",formula_name.get(temp.getValue()));
                node.put("value",temp.getValue());
            }
            else
                node.put("value",-1);
            treeList.add(node);
            if(temp.getLeft()!=null)
            {
                queue.add(temp.getLeft());
                parentQueue.add(id);
            }
            if(temp.getRight()!=null)
            {
                queue.add(temp.getRight());
                parentQueue.add(id);
            }
            id++;
        }
        result.put("list",treeList);
        return result;
    }
}


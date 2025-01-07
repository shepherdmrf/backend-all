package shu.xai.dataAnalysis.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.entity.PageResult;
import shu.xai.dataAnalysis.mapper.MaterialDataMapper;
import shu.xai.dataAnalysis.service.MaterialDataService;
import shu.xai.材料.service.CallPython.CallPythonScript;


import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MaterialDataServiceImpl implements MaterialDataService {

    @Resource
    private MaterialDataMapper materialDataMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final List<String> VALID_ATTRIBUTES = Arrays.asList(
            "Occu_6b", "Occu_18e", "Occu_36f", "C_Na",
            "Occu_M1", "Occu_M2", "EN_M1", "EN_M2",
            "avg_EN_M", "Radius_M1", "Radius_M2", "avg_Radius_M",
            "Valence_M1", "Valence_M2", "avg_Valence_M",
            "Occu_X1", "Occu_X2", "EN_X1", "EN_X2",
            "avg_EN_X", "Radius_X1", "Radius_X2", "avg_Radius_X",
            "Valence_X1", "Valence_X2", "avg_Valence_X",
            "a", "c", "V", "V_MO6", "V_XO4",
            "V_Na1_O6", "V_Na2_O8", "V_Na3_O5",
            "BT2", "BT1", "Min_BT", "RT",
            "Entropy_6b", "Entropy_18e", "Entropy_36f",
            "Entropy_Na", "Entropy_M", "Entropy_X",
            "T","BVSE_energy_barrier"
    );

    @Override
    public PageResult<Map<String, Object>> getPaginatedData(int page, int size) {
        // 计算分页偏移量
        int offset = (page - 1) * size;

        // 查询分页数据
        String sql = "SELECT * FROM data.data2 LIMIT ? OFFSET ?";
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql, size, offset);

        // 获取总记录数
        String countSql = "SELECT COUNT(*) FROM data.data2";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);

        // 返回分页结果
        return new PageResult<>(dataList, totalCount);
    }

    public Map<String, Object> getAttributeStatistics(String attribute) {
        // 校验字段
        if (!isValidAttribute(attribute)) {
            throw new IllegalArgumentException("Invalid attribute: " + attribute);
        }

        // 查询统计信息
        Double max = materialDataMapper.getMaxValue(attribute);
        Double min = materialDataMapper.getMinValue(attribute);
        Double avg = materialDataMapper.getAverageValue(attribute);

        // 查询中位数
        Double median;
        int nonNullCount = materialDataMapper.getNonNullCount(attribute);
        if (nonNullCount == 0) {
            median = null; // 没有非空值
        } else {
            int offset = nonNullCount / 2;
            median = materialDataMapper.getMedianValueWithOffset(attribute, offset);
        }

        // 返回结果
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("max", max);
        statistics.put("min", min);
        statistics.put("avg", avg);
        statistics.put("median", median);
        statistics.put("nullCount", nonNullCount); // 添加空值数量统计

        return statistics;
    }


    @Override
    public Map<String, Map<String, Object>> getStatistics() {
        Map<String, Map<String, Object>> statistics = new HashMap<>();

        // 条件属性
        String[] conditionAttributes = {
                "Occu_6b", "Occu_18e", "Occu_36f", "C_Na",
                "Occu_M1", "Occu_M2", "EN_M1", "EN_M2",
                "avg_EN_M", "Radius_M1", "Radius_M2", "avg_Radius_M",
                "Valence_M1", "Valence_M2", "avg_Valence_M",
                "Occu_X1", "Occu_X2", "EN_X1", "EN_X2",
                "avg_EN_X", "Radius_X1", "Radius_X2", "avg_Radius_X",
                "Valence_X1", "Valence_X2", "avg_Valence_X",
                "a", "c", "V", "V_MO6", "V_XO4",
                "V_Na1_O6", "V_Na2_O8", "V_Na3_O5",
                "BT2", "BT1", "Min_BT", "RT",
                "Entropy_6b", "Entropy_18e", "Entropy_36f",
                "Entropy_Na", "Entropy_M", "Entropy_X",
                "T"
        };

        // 决策属性
        String[] decisionAttributes = {"BVSE_energy_barrier"};

        // 统计条件属性
        Map<String, Object> conditionStats = new HashMap<>();
        for (String attribute : conditionAttributes) {
            conditionStats.put(attribute, getAttributeStatistics(attribute));
        }
        statistics.put("condition", conditionStats);

        // 统计决策属性
        Map<String, Object> decisionStats = new HashMap<>();
        for (String attribute : decisionAttributes) {
            decisionStats.put(attribute, getAttributeStatistics(attribute));
        }
        statistics.put("decision", decisionStats);

        Map<String, Object> counts = new HashMap<>();
        List<String> columnNames = materialDataMapper.getColumnNames();
        int conditionCount = columnNames.size() - 1;
        int decisionCount = 1;
        counts.put("conditionCount", conditionCount);
        counts.put("decisionCount", decisionCount);
        statistics.put("counts", counts);


        return statistics;
    }

    @Override
    public File generateViolin() {
        try {
            // 调用 Python 脚本
            CallPythonScript.CallViolin();

            // 假设 Python 脚本生成的图片文件路径如下
            String imagePath = "./src/main/resources/python/MultifacetedModeling/violin.png";

            // 检查生成的文件是否存在
            File generatedImage = new File(imagePath);
            if (!generatedImage.exists()) {
                throw new RuntimeException("Python script did not generate the image.");
            }

            // 返回生成的图片文件
            return generatedImage;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while generating the Violin", e);
        }
    }

    public Map<String, Integer> getAttributeCounts() {
        // 获取数据库表的元数据
        List<String> columnNames = materialDataMapper.getColumnNames();

        // 条件属性为所有列去掉最后一列
        int conditionCount = columnNames.size() - 1;

        // 决策属性为最后一列
        int decisionCount = 1;

        // 返回统计信息
        Map<String, Integer> counts = new HashMap<>();
        counts.put("conditionCount", conditionCount);
        counts.put("decisionCount", decisionCount);

        return counts;
    }



    /**
     * 校验字段是否合法
     */
    private boolean isValidAttribute(String attribute) {
        for (String validAttribute : VALID_ATTRIBUTES) {
            if (validAttribute.equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}




package shu.xai.characteristicClusterConstruction.service;

import shu.xai.characteristicClusterConstruction.entity.ExcelRowData;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FeatureService {

    // 查询 MLR 数据
    List<Feature> getMLRData();

    // 查询 KNN 数据
    List<Feature> getKNNData();

    // 查询 SVR 数据
    List<Feature> getSVRData();

    // 查询 GPR 数据
    List<Feature> getGPRData();

    // 根据模型查询 KeyKnowledgeFeature 数据
    List<KeyKnowledgeFeature> getKeyKnowledgeByModel(String model);

    List<Map<String, Object>> processAndHandleExcel(String model);

    List<ExcelRowData> processBestAndStoreInExcel(String model, String features);

    File generateFeatureVenn(String model, int Id);
}

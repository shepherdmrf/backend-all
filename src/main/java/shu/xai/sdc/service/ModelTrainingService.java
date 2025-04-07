package shu.xai.sdc.service;

import org.springframework.web.multipart.MultipartFile;
import shu.xai.sdc.entity.ModelData;

import java.util.List;

public interface ModelTrainingService {
    List<ModelData> getAllModels();             // 获取所有模型
    ModelData getModelById(Integer id);         // 根据 ID 获取模型
    void addModel(ModelData modelData) throws Exception;     // 添加模型
    void deleteModelAndFiles(Integer id, String username) throws Exception;  // 根据 ID 删除模型
    String getModelIntroduction(String modelPath, String introductionFile) throws Exception; //获取模型介绍
    byte[] getModelArchitecture(String modelPath, String architectureFile) throws Exception; //获取模型架构
    void uploadFile(MultipartFile file, String type) throws Exception;
    void clearTempDir();
}

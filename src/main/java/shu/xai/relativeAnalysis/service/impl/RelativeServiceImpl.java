package shu.xai.relativeAnalysis.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;
import shu.xai.relativeAnalysis.mapper.RelativeMapper;
import shu.xai.relativeAnalysis.service.RelativeService;
import shu.xai.材料.service.CallPython.CallPythonScript;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class RelativeServiceImpl implements RelativeService {

    @Autowired
    private RelativeMapper relativeMapper;

    @Override
    public List<Feature> getSVRCluster() {
        return relativeMapper.getSVRCluster();
    }

    @Override
    public void generateImagesSvr(int id) {
        try {
            // 根据模型和 ID 获取特征数据
            String features1 = relativeMapper.findSVRById(id);

            // 调用 Python 脚本
            CallPythonScript.CallSVRTrainning(features1);
            CallPythonScript.CallSVRAnalysis();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while generating the images", e);
        }
    }

    @Override
    public File getHighDimensionalImage() {
        try {

            String imagePath = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR/plotting/high_dimensional_distribution.png";

            // 检查生成的文件是否存在
            File generatedImage = new File(imagePath);
            if (!generatedImage.exists()) {
                throw new RuntimeException("Python script did not generate the image.");
            }

            // 返回生成的图片文件
            return generatedImage;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while generating the Venn diagram", e);
        }
    }

    @Override
    public File getLowDimensionalImage() {
        try {

            String imagePath = "./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR/plotting/low_dimensional_distribution.png";

            // 检查生成的文件是否存在
            File generatedImage = new File(imagePath);
            if (!generatedImage.exists()) {
                throw new RuntimeException("Python script did not generate the image.");
            }

            // 返回生成的图片文件
            return generatedImage;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while generating the Venn diagram", e);
        }
    }

    private String encodeFileToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }





}

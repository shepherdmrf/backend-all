package shu.xai.characteristicClusterConstruction.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shu.xai.characteristicClusterConstruction.entity.ExcelRowData;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;
import shu.xai.characteristicClusterConstruction.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;
import shu.xai.材料.page.Knowledge;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cluster")
public class ClusterController {

    private final FeatureService featureService;

    @Autowired
    public ClusterController(FeatureService featureService) {
        this.featureService = featureService;
    }

    // 查询 MLR 数据
    @GetMapping("/features/mlr")
    public List<Feature> getMLRData() {
        return featureService.getMLRData();
    }

    // 查询 KNN 数据
    @GetMapping("/features/knn")
    public List<Feature> getKNNData() {
        return featureService.getKNNData();
    }

    // 查询 SVR 数据
    @GetMapping("/features/svr")
    public List<Feature> getSVRData() {
        return featureService.getSVRData();
    }

    // 查询 GPR 数据
    @GetMapping("/features/gpr")
    public List<Feature> getGPRData() {
        return featureService.getGPRData();
    }

    // 根据 model 查询 KeyKnowledgeFeature 数据
    @GetMapping("/features/keyKnowledge")
    public List<KeyKnowledgeFeature> getKeyKnowledgeByModel(@RequestParam String model) {
        return featureService.getKeyKnowledgeByModel(model);
    }

    @RequestMapping("/training")
    public List<Map<String, Object>> training(@RequestParam String model) {

        return featureService.processAndHandleExcel(model);
    }

    @GetMapping("/bestcluster")
    public List<ExcelRowData> processBestAndStoreInExcel(
            @RequestParam String model,                // 接收 model
            @RequestParam String features
    ) {
        // 打印日志，确认接收到的数据
        String decodedFeatures = null;
        try {
            decodedFeatures = URLDecoder.decode(features, StandardCharsets.UTF_8.name());

            System.out.println("Model: " + model);
            System.out.println("Decoded Features: " + decodedFeatures);

            // 返回结果
            return featureService.processBestAndStoreInExcel(model, decodedFeatures);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String model) throws IOException {
        String fileName = null;

        // 根据 model 名称确定文件名
        switch (model.toLowerCase()) {
            case "mlr":
                fileName = "mlr.xlsx";
                break;
            case "knn":
                fileName = "knn.xlsx";
                break;
            case "svm":
                fileName = "svr.xlsx";
                break;
            case "gpr":
                fileName = "gpr.xlsx";
                break;
            default:
                throw new IllegalArgumentException("Unknown model name: " + model);
        }

        // 文件存储目录
        Path dir = Paths.get("./src/main/resources/python/MultifacetedModeling/Results/OnAllTrainKernel/DKNCOR/Know+Pos-Kernel");
        Path filePath = dir.resolve(fileName).normalize();  // 获取文件的完整路径

        // 创建资源对象
        Resource resource = new UrlResource(filePath.toUri());

        // 检查文件是否存在
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();  // 如果文件不存在，返回 404
        }

        // 获取文件的 MIME 类型
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";  // 默认 MIME 类型
        }

        // 设置响应头，返回文件内容
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/featureVenn")
    public ResponseEntity<Resource> getFeatureVenn(@RequestParam String model, @RequestParam int Id) {
        try {
            // 调用服务层处理特征
            File generatedImage = featureService.generateFeatureVenn(model, Id);

            if (generatedImage == null || !generatedImage.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new InputStreamResource(new ByteArrayInputStream("Failed to generate image".getBytes())));
            }

            // 读取生成的图片并返回
            InputStreamResource resource = new InputStreamResource(new FileInputStream(generatedImage));

            // 设置响应头和内容类型，返回图片
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + generatedImage.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputStreamResource(new ByteArrayInputStream("An error occurred while generating the image".getBytes())));
        }
    }
}

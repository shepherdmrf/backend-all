package shu.xai.relativeAnalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;
import shu.xai.relativeAnalysis.service.RelativeService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/relative")
public class RelativeController {

    @Autowired
    private RelativeService relativeService;

//    @GetMapping("/svr")
//    public List<KeyKnowledgeFeature> getSVRCluster() {
//        return relativeService.getSVRCluster();
//    }
//
//    @GetMapping("/gpr")
//    public List<KeyKnowledgeFeature> getGPRCluster() {
//        return relativeService.getGPRCluster();
//    }

    @GetMapping("/getDataUrls")
    public ResponseEntity<Map<String, String>> getDataUrls() {
        try {
            // 生成 JSON 数据（如果需要的话）
            relativeService.generateImagesSvr();

            // JSON 数据 URL
            String highDimensionalDataUrl = "/relative/highDimensionalData";
            String FValueDataUrl = "/relative/FValueData";

            // 返回 JSON 数据的 URL
            Map<String, String> dataUrls = new HashMap<>();
            dataUrls.put("highDimensionalData", highDimensionalDataUrl);
            dataUrls.put("FValueData", FValueDataUrl);

            return ResponseEntity.ok(dataUrls);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while generating the data URLs");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

//    @GetMapping("/highDimensionalImage")
//    public ResponseEntity<Resource> getHighDimensionalImage() {
//        try {
//            // 获取高维图片文件
//            File file = relativeService.getHighDimensionalImage();
//
//            if (file == null || !file.exists()) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(new InputStreamResource(new ByteArrayInputStream("Failed to generate image".getBytes())));
//            }
//
//            // 读取生成的图片并返回
//            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_PNG)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
//                    .body(resource);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    @GetMapping("/FValueImage")
//    public ResponseEntity<Resource> getFValueImage() {
//        try {
//            // 获取低维图片文件
//            File file = relativeService.getFValueImage();
//
//            if (file == null || !file.exists()) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(new InputStreamResource(new ByteArrayInputStream("Failed to generate image".getBytes())));
//            }
//
//            // 读取生成的图片并返回
//            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_PNG)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
//                    .body(resource);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @GetMapping("/highDimensionalData")
    public ResponseEntity<String> getHighDimensionalData() {
        try {
            // 读取 scatter_data.json
            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR/scatter_data.json")));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to load data\"}");
        }
    }

    @GetMapping("/FValueData")
    public ResponseEntity<String> getFValueData() {
        try {
            // 读取 feature_importance.json
            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR/feature_importance.json")));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to load data\"}");
        }
    }

    @RequestMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {

        // 文件存储目录
        Path filePath = Paths.get("./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR/cluster_features.xlsx");
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
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/getGPRUrls")
    public ResponseEntity<Resource> getGPRUrls() {
        try {
            // 生成图片
            relativeService.generateImagesGpr();

            File file = relativeService.getGPRImage();

            if (file == null || !file.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }

            // 返回图片流
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}





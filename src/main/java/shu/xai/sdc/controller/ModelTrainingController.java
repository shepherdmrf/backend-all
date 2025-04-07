package shu.xai.sdc.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.sdc.entity.ModelData;
import shu.xai.sdc.service.ModelTrainingService;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/model-training")
public class ModelTrainingController {

        private final ModelTrainingService modelTrainingService;

        public ModelTrainingController(ModelTrainingService modelTrainingService) {
                this.modelTrainingService = modelTrainingService;
        }

        // 获取所有模型
        @GetMapping("/models")
        public List<ModelData> getAllModels() {
                return modelTrainingService.getAllModels();
        }

        // 根据 ID 获取模型
        @GetMapping("/models/{id}")
        public ModelData getModelById(@PathVariable Integer id) {
                return modelTrainingService.getModelById(id);
        }

        // 添加模型
        @PostMapping("/add_model")
        public ResponseEntity<String> addModel(@RequestBody ModelData modelData) {
                try {
                        modelTrainingService.addModel(modelData);
                        return ResponseEntity.ok("模型添加成功");
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("模型添加失败: " + e.getMessage());
                }
        }

        // 删除模型
        @DeleteMapping("/models/{id}")
        public ResponseEntity<String> deleteModelAndFiles(
                @PathVariable Integer id,
                @RequestParam String username) {  // 获取前端传递的用户名
                try {
                        modelTrainingService.deleteModelAndFiles(id, username);
                        return ResponseEntity.ok("模型和相关数据删除成功");
                } catch (Exception e) {
                        return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
                }
        }

        //获取模型介绍
        @GetMapping("/model-introduction")
        public ResponseEntity<String> getModelIntroduction(
                @RequestParam String modelPath,
                @RequestParam String introductionFile) {
                try {
                        String content = modelTrainingService.getModelIntroduction(modelPath, introductionFile);
                        return ResponseEntity.ok(content);
                } catch (Exception e) {
                        return ResponseEntity.status(500).body("Error: " + e.getMessage());
                }
        }

        //获取模型架构
        @GetMapping("/model-structure")
        public ResponseEntity<byte[]> getModelStructure(
                @RequestParam String modelPath,
                @RequestParam String architectureFile) {
                try {
                        byte[] imageData = modelTrainingService.getModelArchitecture(modelPath, architectureFile);
                        return ResponseEntity.ok().header("Content-Type", "image/png").body(imageData);
                } catch (Exception e) {
                        return ResponseEntity.status(500).body(null);
                }
        }

        @GetMapping("/download")
        public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) throws IOException {
                Path filePath = Paths.get("src/main/resources/static", fileName);

                if (!Files.exists(filePath)) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }

                byte[] fileData = Files.readAllBytes(filePath);
                String mimeType = Files.probeContentType(filePath);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : "application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                        .body(fileData);
        }

        @PostMapping("/upload")
        public ResponseEntity<Map<String, Object>> uploadFile(
                @RequestParam("file") MultipartFile file,
                @RequestParam("type") String type) {

                Map<String, Object> response = new HashMap<>();
                try {
                        modelTrainingService.uploadFile(file, type);
                        response.put("code", 1);
                        response.put("msg", "上传成功");
                        return ResponseEntity.ok(response);
                } catch (IllegalArgumentException e) {
                        response.put("code", 0);
                        response.put("msg", e.getMessage());
                        return ResponseEntity.badRequest().body(response);
                } catch (Exception e) {
                        response.put("code", 0);
                        response.put("msg", "文件上传失败: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
        }

        @PostMapping("/clear-temp")
        public ResponseEntity<String> clearTempFiles() {
                try {
                        modelTrainingService.clearTempDir();
                        return ResponseEntity.ok("临时文件删除成功");
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除临时文件失败：" + e.getMessage());
                }
        }
}

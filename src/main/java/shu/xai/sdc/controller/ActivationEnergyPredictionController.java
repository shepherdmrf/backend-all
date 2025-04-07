package shu.xai.sdc.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shu.xai.sdc.service.ActivationEnergyPredictionService;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activation-energy")
public class ActivationEnergyPredictionController {

    private final ActivationEnergyPredictionService predictionService;

    public ActivationEnergyPredictionController(ActivationEnergyPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public Map<String, Object> predictActivationEnergy(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String userName = (String) requestData.get("user_name");
            List<String> filePaths = (List<String>) requestData.get("file_paths");
            String modelName = (String) requestData.get("model_name");

            if (userName == null || filePaths == null || filePaths.isEmpty() || modelName == null) {
                response.put("status", "error");
                response.put("message", "Invalid input: 'user_name', 'file_paths', and 'model_name' are required.");
                return response;
            }

            // 调用服务处理预测
            List<Map<String, Object>> results = predictionService.predictActivationEnergy(userName, filePaths, modelName);
            response.put("status", "success");
            response.put("results", results);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            System.err.println("预测失败：" + e.getMessage());
        }

        return response;
    }

    @GetMapping("/results")
    public ResponseEntity<?> getPredictionResults(@RequestParam String username, @RequestParam String model_name) {
        try {
            List<Map<String, Object>> results = predictionService.getPredictionResults(username, model_name);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 1);
            response.put("results", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 0);
            errorResponse.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/download-results")
    public ResponseEntity<byte[]> downloadResults(@RequestParam String userName, @RequestParam String model_name) {
        try {
            // 调用 Service 获取结果文件
            File resultsFile = predictionService.getResultsFile(userName, model_name);

            // 读取文件内容
            byte[] fileContent = Files.readAllBytes(resultsFile.toPath());

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", model_name + "_results.csv");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            // 文件未找到时返回 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // 其他异常返回 500
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
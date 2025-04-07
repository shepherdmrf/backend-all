package shu.xai.sdc.controller;

import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shu.xai.sdc.service.InterstitialNetworkRepresentationService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/interstitial_network")
public class InterstitialNetworkRepresentationController {

    @Autowired
    private InterstitialNetworkRepresentationService interstitialNetworkService;

    @PostMapping("/convert")
    public ResponseEntity<Map<String, String>> convertFiles(@RequestBody Map<String, Object> request) {
        // 提取前端传递的参数
        List<String> filePaths = (List<String>) request.get("file_paths");
        String userName = (String) request.get("user_name");
        String migrantIon = (String) request.get("migrant_ion");

        // 打印收到的参数
        System.out.println("Received data from frontend:");
        System.out.println("File Paths: " + filePaths);
        System.out.println("User Name: " + userName);
        System.out.println("Migrant Ion: " + migrantIon);

        try {
            // 调用 Service 方法进行处理
            Map<String, String> results = interstitialNetworkService.convertFiles(filePaths, userName, migrantIon);

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            // 捕获异常并返回错误响应
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);

        }
    }

    @PostMapping("/getCifContentandSpt")
    public ResponseEntity<Map<String, String>> getCifContentandSpt(@RequestBody Map<String, Object> requestData) {
        String fileNames = (String) requestData.get("fileNames");
        String username = (String) requestData.get("username");

        try {
            Map<String, String> result = interstitialNetworkService.getCifContentandSpt(fileNames, username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/filePreview")
    public ResponseEntity<?> previewFile(
            @RequestParam("userName") String userName,
            @RequestParam("fileName") String fileName
    ) {
        try {
            // 检查参数
            if (userName == null || userName.isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空！");
            }
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body("文件名不能为空！");
            }

            // 调用 Service 方法获取文件内容和统计信息
            Map<String, Object> fileData = interstitialNetworkService.previewFile(userName, fileName);

            return ResponseEntity.ok(fileData);
        } catch (Exception e) {
            System.err.println("文件预览失败：" + e.getMessage());
            return ResponseEntity.status(500).body("文件预览失败：" + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFiles(
            @RequestParam("userName") String userName,
            @RequestParam("fileNames") String fileNames) {
        try {
            // 打印接收到的参数
            System.out.println("Received userName: " + userName);
            System.out.println("Received fileNames: " + fileNames);

            // 将逗号分隔的字符串转换为文件名列表
            List<String> fileNameList = Arrays.asList(fileNames.split(","));

            // 调用服务层获取压缩文件数据
            byte[] zipData = interstitialNetworkService.downloadFiles(userName, fileNameList);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

            // 返回压缩包文件
            System.out.println("Download successful, returning ZIP file.");
            return new ResponseEntity<>(zipData, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during file download: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}

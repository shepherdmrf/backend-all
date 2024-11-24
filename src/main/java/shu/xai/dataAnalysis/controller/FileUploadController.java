package shu.xai.dataAnalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.dataAnalysis.service.FileUploadService;

@RestController
@RequestMapping("/data")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize());
        System.out.println("文件类型: " + file.getContentType());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空");
        }



        try {
            fileUploadService.processFile(file); // 处理文件并保存到数据库
            return ResponseEntity.ok("文件上传并成功保存到数据库！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("文件处理失败：" + e.getMessage());
        }
    }
}

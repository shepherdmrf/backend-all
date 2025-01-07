package shu.xai.dataAnalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.dataAnalysis.service.FileUploadService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/data")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    private static final String UPLOAD_FOLDER = "./src/main/resources/python/MultifacetedModeling/DataInput"; // 修改为你想要保存文件的目录

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize());
        System.out.println("文件类型: " + file.getContentType());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空");
        }

        String fixedname="uploadedFile.xlsx";
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + File.separator + fixedname);
            Files.write(path, bytes);
            System.out.println( "File uploaded successfully: " +fixedname);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println( "Failed to upload file: " +fixedname);
        }

        try {
            fileUploadService.processFile(file); // 处理文件并保存到数据库
            return ResponseEntity.ok("文件上传并成功保存到数据库！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("文件处理失败：" + e.getMessage());
        }
    }
}

package shu.xai.材料.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class FileDownloadController {

    @GetMapping("/download/knowledge")
    public ResponseEntity<InputStreamResource> downloadKnowledge(@RequestParam String address) throws IOException {
        // 创建一个字节流来存储ZIP文件内容
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        // 拼接文件路径
        String directoryPath = "./src/main/resources/python/MultifacetedModeling/Results/OnTrain/KnowledgeKernel/" + address;
        File directory = new File(directoryPath);

        // 确保目录存在且是目录
        if (directory.exists() && directory.isDirectory()) {
            // 遍历目录中的所有文件
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        // 如果是文件，添加到压缩包中
                        if (file.isFile()) {
                            FileInputStream fileInputStream = new FileInputStream(file);
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zipOutputStream.putNextEntry(zipEntry);

                            // 将文件内容写入到ZIP文件中
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fileInputStream.read(buffer)) > 0) {
                                zipOutputStream.write(buffer, 0, length);
                            }
                            fileInputStream.close();
                            zipOutputStream.closeEntry();
                        }
                    }
                }
            }
        } else {
            throw new FileNotFoundException("指定的目录不存在或不是有效的目录！");
        }

        zipOutputStream.finish();
        zipOutputStream.close();

        // 创建InputStreamResource来作为文件下载的响应体
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        // 返回压缩文件的响应
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=files.zip")
                .body(resource);
    }
    @GetMapping("/download/zip")
    public ResponseEntity<InputStreamResource> downloadZip(@RequestParam String address) throws IOException {
        // 创建一个字节流来存储ZIP文件内容
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        // 拼接文件路径
        String directoryPath =address;
        File directory = new File(directoryPath);
        // 确保目录存在且是目录
        if (directory.exists() && directory.isDirectory()) {
            // 遍历目录中的所有文件
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        // 如果是文件，添加到压缩包中
                        if (file.isFile()) {
                            FileInputStream fileInputStream = new FileInputStream(file);
                            ZipEntry zipEntry = new ZipEntry(file.getName());
                            zipOutputStream.putNextEntry(zipEntry);

                            // 将文件内容写入到ZIP文件中
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fileInputStream.read(buffer)) > 0) {
                                zipOutputStream.write(buffer, 0, length);
                            }
                            fileInputStream.close();
                            zipOutputStream.closeEntry();
                        }
                    }
                }
            }
        } else {
            throw new FileNotFoundException("指定的目录不存在或不是有效的目录！");
        }

        zipOutputStream.finish();
        zipOutputStream.close();

        // 创建InputStreamResource来作为文件下载的响应体
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        // 返回压缩文件的响应
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=files.zip")
                .body(resource);
    }

    @GetMapping("/download/output")
    public ResponseEntity<InputStreamResource> downloadOutput(@RequestParam String filename) throws IOException {
        // 创建一个字节流来存储ZIP文件内容
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        String address = "./src/main/resources/python/MultifacetedModeling/DataOutput";
        File directory = new File(address);

        // 确保目录存在且是目录
        if (directory.exists() && directory.isDirectory()) {
            // 遍历第一级子目录
            File[] subdirectories = directory.listFiles(File::isDirectory);
            if (subdirectories != null) {
                for (File subdirectory : subdirectories) {
                    File targetFile = new File(subdirectory, filename);

                    // 如果子目录中有目标文件，则将其打包
                    if (targetFile.exists() && targetFile.isFile()) {
                        FileInputStream fileInputStream = new FileInputStream(targetFile);

                        // 为了保留子目录结构，在zip文件中加上子目录结构
                        ZipEntry zipEntry = new ZipEntry(subdirectory.getName() + "/" + filename);
                        zipOutputStream.putNextEntry(zipEntry);

                        // 将文件内容写入到ZIP文件中
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fileInputStream.read(buffer)) > 0) {
                            zipOutputStream.write(buffer, 0, length);
                        }
                        fileInputStream.close();
                        zipOutputStream.closeEntry();
                    }
                }
            }
        } else {
            throw new FileNotFoundException("指定的目录不存在或不是有效的目录！");
        }

        zipOutputStream.finish();
        zipOutputStream.close();

        // 创建InputStreamResource来作为文件下载的响应体
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        // 返回压缩文件的响应
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=files.zip")
                .body(resource);
    }
    @GetMapping("/download/treeCluster")
    public ResponseEntity<Resource> handleDownload(
            @RequestParam String level,
            @RequestParam String node) throws Exception {

        // 1. 根据层级(level)和节点(node)生成或获取文件
        String fileName = String.format("%s-%s.xlsx", level, node);
        String downLoadPath="./src/main/resources/python/MultifacetedModeling/RuleExtraction/KNN/TreeCluster/";
        String excelPath=downLoadPath +fileName;
        Path filePath = Paths.get(excelPath); // 例如: "/data/exports/"

        // 2. 检查文件是否存在
        if (!Files.exists(filePath)) {
            throw new RuntimeException("文件不存在: " + fileName);
        }

        // 3. 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName); // 强制下载
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // 二进制流

        // 4. 返回文件流
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(Files.size(filePath))
                .body(new FileSystemResource(filePath));
    }
}

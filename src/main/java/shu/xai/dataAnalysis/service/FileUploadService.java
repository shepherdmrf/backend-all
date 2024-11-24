package shu.xai.dataAnalysis.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    void processFile(MultipartFile file) throws Exception; // 定义文件处理方法
}

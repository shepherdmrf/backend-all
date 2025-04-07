package shu.xai.sdc.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;


public interface CifUploadService {

    String handleFileUpload(MultipartFile file, String username);

    String getUserFiles(String username);

    String getUserFiles2(String username);

    String deleteFile(String filePath);

    String deleteFiles(List<String> filePaths);

    String continueUpload(MultipartFile file, String username);

    String handleDuplicateFiles(String username, String action) throws Exception;
}

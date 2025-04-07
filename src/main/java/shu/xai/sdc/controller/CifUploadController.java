package shu.xai.sdc.controller;

import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.sdc.service.CifUploadService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;

import com.alibaba.fastjson.JSON;
import java.util.Map;

import java.util.List;


/**
 * Controller for handling CIF file uploads.
 */
@RestController
@RequestMapping("/cifUpload")
public class CifUploadController {

    private static final Logger logger = LoggerFactory.getLogger(CifUploadController.class);

    @Autowired
    private CifUploadService cifUploadService;

    @GetMapping("/getUserFiles")
    public String getUserFiles(@RequestParam("username") String username) {
        try {
            return cifUploadService.getUserFiles(username);
        } catch (Exception e) {
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "获取文件列表失败", null);
        }
    }

    @GetMapping("/getUserFiles2")
    public String getUserFiles2(@RequestParam("username") String username) {
        try {
            return cifUploadService.getUserFiles2(username);
        } catch (Exception e) {
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "获取文件列表失败", null);
        }
    }

    /**
     * Handle CIF ZIP file upload.
     *
     * @param file        Uploaded file
     * @param otherParams Additional parameters as JSON string
     * @return Response result
     */
    @PostMapping("/uploadZip")
    public String uploadZipFile(@RequestParam("file") MultipartFile file,
                                @RequestParam("otherParams") String otherParams) {
        try {
            // 解析 otherParams JSON
            Map<String, Object> params = JSON.parseObject(otherParams, Map.class);

            // 获取用户名
            String username = (String) params.get("username");
            if (username == null || username.isEmpty()) {
                return ResultUtils.commonResult(ResultCodeEnums.VALIDATION_ERROR.getCode(),
                        "用户名不能为空", "请提供用户名！");
            }

            // 调用 Service 层处理文件上传
            return cifUploadService.handleFileUpload(file, username);
        } catch (Exception e) {
            logger.error("Error during file upload", e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "文件上传过程中发生错误");
        }
    }

    /**
     * View CIF file content.
     *
     * @param filePath The path of the CIF file to view.
     * @return Response containing the file content.
     */
    @RequestMapping("/viewFileContent")
    public String viewFileContent(@RequestParam("filePath") String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                        "文件不存在", "无法读取文件内容");
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                    "文件读取成功", content.toString());
        } catch (Exception e) {
            logger.error("读取文件内容时发生错误", e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "读取文件内容失败", "请稍后再试");
        }
    }

    // 删除单个文件
    @PostMapping("/deleteFile")
    public String deleteFile(@RequestBody Map<String, String> requestBody) {
        String filePath = requestBody.get("filePath");
        if (filePath == null || filePath.isEmpty()) {
            return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                    "文件路径为空", "删除失败");
        }
        return cifUploadService.deleteFile(filePath);
    }

    // 批量删除文件
    @PostMapping("/deleteFiles")
    public String deleteFiles(@RequestBody Map<String, List<String>> requestBody) {
        List<String> filePaths = requestBody.get("filePaths");
        if (filePaths == null || filePaths.isEmpty()) {
            return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                    "文件路径列表为空", "删除失败");
        }
        return cifUploadService.deleteFiles(filePaths);
    }

    @PostMapping("/continueUpload")
    public String continueUpload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("otherParams") String otherParams) {
        try {
            // 解析 otherParams JSON
            Map<String, Object> params = JSON.parseObject(otherParams, Map.class);

            // 获取用户名
            String username = (String) params.get("username");
            if (username == null || username.isEmpty()) {
                return ResultUtils.commonResult(ResultCodeEnums.VALIDATION_ERROR.getCode(),
                        "用户名不能为空", "请提供用户名！");
            }

            // 调用 Service 层处理继续上传逻辑
            return cifUploadService.continueUpload(file, username);

        } catch (Exception e) {
            logger.error("Error during continue upload", e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "继续上传失败，请稍后重试！", null);
        }
    }

    @PostMapping("/handleDuplicates")
    public String handleDuplicateFiles(
            @RequestParam("username") String username,
            @RequestParam("action") String action) {
        try {
            // 调用服务层方法处理重复文件
            String result = cifUploadService.handleDuplicateFiles(username, action);
            return result; // 返回服务层的处理结果
        } catch (IllegalArgumentException e) {
            // 参数验证失败时，返回错误信息
            return ResultUtils.commonResult(
                    ResultCodeEnums.INVALID_REQUEST.getCode(),
                    e.getMessage(),
                    null
            );
        } catch (Exception e) {
            // 处理过程中发生未知错误
            return ResultUtils.commonResult(
                    ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "处理重复文件时发生错误，请稍后重试！",
                    null
            );
        }
    }
}
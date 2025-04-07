package shu.xai.sdc.service.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.sdc.service.CifUploadService;
import shu.xai.sys.enums.ResultCodeEnums;
import shu.xai.sys.utils.ResultUtils;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class CifUploadServiceImpl implements CifUploadService {

    private static final Logger logger = LoggerFactory.getLogger(CifUploadServiceImpl.class);
    private static final Map<String, String> userUnzipDirectories = new HashMap<>();
    private static final Map<String, String> userSavedZipFiles = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> userExtractedFiles = new HashMap<>();
    private final String basePath = System.getProperty("user.dir") + File.separator + "CifUpload";

    @Override
    public String getUserFiles(String username) {
        try {
            File userDir = new File(basePath, username);
            if (!userDir.exists() || !userDir.isDirectory()) {
                return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                        "用户目录不存在", null);
            }

            // 获取目录下的文件
            File[] files = userDir.listFiles((dir, name) -> name.endsWith(".cif"));
            Map<String, String> fileNamePathMap = new HashMap<>();
            if (files != null) {
                for (File file : files) {
                    fileNamePathMap.put(file.getName(), file.getAbsolutePath());
                }
            }

            return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                    "文件列表获取成功", JSON.toJSONString(fileNamePathMap));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "获取文件列表时发生错误", null);
        }
    }

    @Override
    public String getUserFiles2(String username) {
        try {
            File userDir = new File(basePath, username);
            if (!userDir.exists() || !userDir.isDirectory()) {
                return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                        "用户目录不存在", null);
            }

            // 获取目录下的文件
            File[] files = userDir.listFiles((dir, name) -> name.endsWith(".cif"));
            Map<String, Object> fileDetailsMap = new HashMap<>();
            if (files != null) {
                for (File file : files) {
                    // 提取文件名和路径
                    String fileName = file.getName();
                    String filePath = file.getAbsolutePath();
                    // 提取化学式
                    String formula = extractChemicalFormula(filePath);

                    // 创建一个包含化学式的文件详情对象
                    Map<String, String> fileDetails = new HashMap<>();
                    fileDetails.put("fileName", fileName);
                    fileDetails.put("filePath", filePath);
                    fileDetails.put("chemicalFormula", formula);

                    fileDetailsMap.put(fileName, fileDetails);
                }
            }

            return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                    "文件列表获取成功", JSON.toJSONString(fileDetailsMap));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "获取文件列表时发生错误", null);
        }
    }

    private String extractChemicalFormula(String cifFilePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(cifFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                // 查找包含 _chemical_formula_sum 的行
                if (line.startsWith("_chemical_formula_sum")) {
                    // 使用正则提取化学式，去除多余的空格
                    String regex = "_chemical_formula_sum\\s+'(.+)'";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        // 提取化学式并去除多余空格
                        return matcher.group(1).trim();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null; // 若没有找到化学式，返回null
    }

    @Override
    public String handleFileUpload(MultipartFile file, String username) {
        if (file.isEmpty()) {
            return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                    ResultCodeEnums.FILE_NULL.getMsg(), "上传的文件为空，请重新选择文件！");
        }

        try {
            // 定义路径
            String basePath = System.getProperty("user.dir") + File.separator + "CifUpload";

            // 拼接用户目录路径
            String userDirPath = basePath + File.separator + username;

            // 创建用户目录（如果不存在）
            File userDir = new File(userDirPath);
            if (!userDir.exists()) {
                boolean dirCreated = userDir.mkdirs();
                if (dirCreated) {
                    logger.info("用户目录已成功创建: {}", userDirPath);
                } else {
                    logger.error("用户目录创建失败: {}", userDirPath);
                    return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                            "用户目录创建失败", "请检查文件系统权限！");
                }
            }

            // 保存上传的 ZIP 文件到 /CifUpload 临时目录
            File savedZip = new File(basePath, file.getOriginalFilename());
            file.transferTo(savedZip);
            logger.info("ZIP 文件已保存到: {}", savedZip.getAbsolutePath());

            // 解压 ZIP 文件到 /CifUpload/{zipFileName}_unzip 临时目录
            File unzipDir = new File(basePath, savedZip.getName().replace(".zip", "_unzip"));
            List<String> extractedFiles = extractZipFile(savedZip, unzipDir);

            // 检查解压后的文件类型并移动 .cif 文件到 /CifUpload/用户名
            List<String> duplicateFiles = new ArrayList<>();
            for (String filePath : extractedFiles) {
                File extractedFile = new File(filePath);
                if (filePath.endsWith(".cif")) {
                    File targetFile = new File(userDir, extractedFile.getName());
                    if (targetFile.exists()) {
                        duplicateFiles.add(extractedFile.getName());
                        logger.warn("文件名重复，已跳过: {}", extractedFile.getName());
                    } else {
                        Files.move(extractedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        logger.info("文件已移动到用户目录: {}", targetFile.getAbsolutePath());
                    }
                } else {
                    logger.warn("发现不合法的文件类型: {}", extractedFile.getName());
                }
            }

            // 删除临时 ZIP 文件和解压后的临时文件夹
            savedZip.delete();
            deleteDirectory(unzipDir);

            // 准备返回数据
            Map<String, String> fileNamePathMap = new HashMap<>();
            for (File cifFile : userDir.listFiles()) {
                if (cifFile.isFile() && cifFile.getName().endsWith(".cif")) {
                    fileNamePathMap.put(cifFile.getName(), cifFile.getAbsolutePath());
                }
            }

            if (duplicateFiles.isEmpty()) {
                return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                        "文件上传成功！", JSON.toJSONString(fileNamePathMap));
            } else {
                return ResultUtils.commonResult(ResultCodeEnums.PARTIAL_SUCCESS.getCode(),
                        "部分文件名重复，已跳过", JSON.toJSONString(fileNamePathMap));
            }

        } catch (Exception e) {
            logger.error("处理文件上传时出错", e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "处理文件上传时发生错误，请稍后重试！");
        }
    }

    /**
     * 根据用户 ID 获取用户名
     *
     * @param userId 用户 ID
     * @return 用户名
     */

    /**
     * 解压 ZIP 文件到指定目录，并返回解压的文件名列表。
     *
     * @param zipFile   待解压的 ZIP 文件
     * @param targetDir 解压目标目录
     * @return 解压后的文件名列表
     * @throws IOException 如果解压过程中发生错误
     */
    private List<String> extractZipFile(File zipFile, File targetDir) throws IOException {
        List<String> extractedFiles = new ArrayList<>();
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                File extractedFile = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    // 创建文件夹
                    extractedFile.mkdirs();
                    logger.info("创建文件夹: {}", extractedFile.getAbsolutePath());
                } else {
                    // 创建父文件夹
                    extractedFile.getParentFile().mkdirs();
                    // 写入文件内容
                    try (InputStream inputStream = zip.getInputStream(entry);
                         OutputStream outputStream = new FileOutputStream(extractedFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                    extractedFiles.add(extractedFile.getAbsolutePath());
                    logger.info("解压文件: {}", extractedFile.getAbsolutePath());
                }
            }
        }
        return extractedFiles;
    }

    @Override
    public String deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    logger.info("文件删除成功: {}", filePath);
                    return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                            "文件删除成功", null);
                } else {
                    logger.error("文件删除失败: {}", filePath);
                    return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                            "文件删除失败", null);
                }
            } else {
                logger.warn("文件不存在: {}", filePath);
                return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                        "文件不存在", null);
            }
        } catch (Exception e) {
            logger.error("删除文件时发生错误: {}", filePath, e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "文件删除时发生错误", null);
        }
    }

    @Override
    public String deleteFiles(List<String> filePaths) {
        List<String> failedDeletes = new ArrayList<>();
        try {
            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    if (!file.delete()) {
                        failedDeletes.add(filePath);
                        logger.error("文件删除失败: {}", filePath);
                    } else {
                        logger.info("文件删除成功: {}", filePath);
                    }
                } else {
                    failedDeletes.add(filePath);
                    logger.warn("文件不存在: {}", filePath);
                }
            }

            if (failedDeletes.isEmpty()) {
                return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                        "所有选中文件删除成功", null);
            } else {
                return ResultUtils.commonResult(ResultCodeEnums.PARTIAL_SUCCESS.getCode(),
                        "部分文件删除失败", JSON.toJSONString(failedDeletes));
            }
        } catch (Exception e) {
            logger.error("批量删除文件时发生错误", e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    "批量删除时发生错误", null);
        }
    }

    @Override
    public String continueUpload(MultipartFile file, String username) {
        if (file.isEmpty()) {
            return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                    ResultCodeEnums.FILE_NULL.getMsg(), "上传的文件为空，请重新选择文件！");
        }

        try {
            // 定义基础路径
            String basePath = System.getProperty("user.dir") + File.separator + "CifUpload";

            // 用户目录路径
            File userDir = new File(basePath, username);
            if (!userDir.exists() || !userDir.isDirectory()) {
                return ResultUtils.commonResult(ResultCodeEnums.FILE_NULL.getCode(),
                        "用户目录不存在", "请先上传主 ZIP 文件后再继续上传！");
            }

            // 保存上传的 ZIP 文件到 /CifUpload
            File tempDir = new File(basePath); // 临时目录
            File savedZip = new File(tempDir, file.getOriginalFilename());
            file.transferTo(savedZip);
            logger.info("继续上传的 ZIP 文件已保存到: {}", savedZip.getAbsolutePath());
            userSavedZipFiles.put(username, savedZip.getAbsolutePath()); // 保存 ZIP 文件路径

            // 解压 ZIP 文件到 /CifUpload/{zipFileName}_unzip 临时目录
            File unzipDir = new File(tempDir, savedZip.getName().replace(".zip", "_unzip"));
            userUnzipDirectories.put(username, unzipDir.getAbsolutePath()); // 保存解压目录路径
            List<String> extractedFiles = extractZipFile(savedZip, unzipDir);
            userExtractedFiles.put(username, extractedFiles); // 保存解压文件列表

            // 检查是否存在重复文件
            List<String> duplicateFiles = new ArrayList<>();
            for (String filePath : extractedFiles) {
                File extractedFile = new File(filePath);
                if (extractedFile.getName().endsWith(".cif")) {
                    File targetFile = new File(userDir, extractedFile.getName());
                    if (targetFile.exists()) {
                        duplicateFiles.add(extractedFile.getName());
                    }
                }
            }

            // 如果存在重复文件，记录并返回 DUPLICATE_FILES 状态码
            if (!duplicateFiles.isEmpty()) {
                logger.warn("发现重复文件: {}", duplicateFiles);
                return ResultUtils.commonResult(ResultCodeEnums.DUPLICATE_FILES.getCode(),
                        "存在重复文件，等待用户确认操作",
                        JSON.toJSONString(duplicateFiles));
            }

            // 如果没有重复文件，继续移动文件到用户目录
            for (String filePath : extractedFiles) {
                File extractedFile = new File(filePath);
                if (extractedFile.getName().endsWith(".cif")) {
                    File targetFile = new File(userDir, extractedFile.getName());
                    Files.move(extractedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("文件已移动到用户目录: {}", targetFile.getAbsolutePath());
                } else {
                    logger.warn("发现不合法的文件类型: {}", extractedFile.getName());
                }
            }

            // 删除临时 ZIP 文件和解压后的临时文件夹
            savedZip.delete();
            deleteDirectory(unzipDir);

            // 返回更新后的文件列表
            Map<String, String> fileNamePathMap = new HashMap<>();
            for (File cifFile : userDir.listFiles()) {
                if (cifFile.isFile() && cifFile.getName().endsWith(".cif")) {
                    fileNamePathMap.put(cifFile.getName(), cifFile.getAbsolutePath());
                }
            }

            return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                    "文件上传成功！", JSON.toJSONString(fileNamePathMap));

        } catch (Exception e) {
            logger.error("处理继续上传时发生错误", e);
            return ResultUtils.commonResult(ResultCodeEnums.UNKNOWN_ERROR.getCode(),
                    ResultCodeEnums.UNKNOWN_ERROR.getMsg(), "处理继续上传时发生错误，请稍后重试！");
        }
    }

    @Override
    public String handleDuplicateFiles(String username, String action) throws Exception {
        // 定义基础路径
        String basePath = System.getProperty("user.dir") + File.separator + "CifUpload";
        File userDir = new File(basePath, username);

        if (!userDir.exists() || !userDir.isDirectory()) {
            throw new IllegalArgumentException("用户目录不存在，请先上传主 ZIP 文件！");
        }

        // 从保存的 Map 中获取临时解压目录和 ZIP 文件路径
        String unzipDirPath = userUnzipDirectories.get(username);
        String savedZipPath = userSavedZipFiles.get(username);
        if (unzipDirPath == null || savedZipPath == null) {
            throw new IllegalArgumentException("未找到对应的解压目录或 ZIP 文件，请先上传文件！");
        }

        File unzipDir = new File(unzipDirPath);
        File savedZip = new File(savedZipPath);

        if (!unzipDir.exists() || !unzipDir.isDirectory()) {
            throw new IllegalArgumentException("临时解压目录不存在，无法继续操作！");
        }

        // 获取解压目录下的所有文件
        List<String> extractedFiles = userExtractedFiles.get(username);

        // 处理文件逻辑
        for (String filePath : extractedFiles) {
            File extractedFile = new File(filePath);
            if (extractedFile.getName().endsWith(".cif")) {
                File targetFile = new File(userDir, extractedFile.getName());
                try {
                    if (targetFile.exists() && "skip".equalsIgnoreCase(action)) {
                        logger.info("跳过文件: {}", extractedFile.getName());
                        continue;
                    }
                    Files.move(extractedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("{} 文件成功移动到用户目录: {}", action.equals("overwrite") ? "覆盖" : "正常添加", targetFile.getAbsolutePath());
                } catch (IOException e) {
                    logger.error("文件移动失败: {}", extractedFile.getName(), e);
                }
            }
        }

        // 删除临时 ZIP 文件和解压目录
        savedZip.delete();
        deleteDirectory(unzipDir);

        // 从 Map 中移除解压目录路径
        userSavedZipFiles.remove(username);
        userUnzipDirectories.remove(username);
        userExtractedFiles.remove(username);

        // 返回更新后的文件列表
        Map<String, String> fileNamePathMap = new HashMap<>();
        for (File cifFile : userDir.listFiles()) {
            if (cifFile.isFile() && cifFile.getName().endsWith(".cif")) {
                fileNamePathMap.put(cifFile.getName(), cifFile.getAbsolutePath());
            }
        }

        return ResultUtils.commonResult(ResultCodeEnums.SUCCESS.getCode(),
                "文件上传成功！", JSON.toJSONString(fileNamePathMap));
    }

    /**
     * 删除文件夹及其内容。
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * 根据用户名和基础路径查找主 ZIP 文件解压生成的目录。
     *
     * @param username 用户名，用于匹配文件夹前缀
     * @param basePath 基础路径，主目录的根路径
     * @return 主目录 File 对象，未找到时返回 null
     */
    private File findExistingCifDirectory(String username, String basePath) {
        File baseDir = new File(basePath);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return null;
        }

        // 遍历子目录，匹配用户名前缀
        File[] directories = baseDir.listFiles(File::isDirectory);
        if (directories != null) {
            for (File dir : directories) {
                if (dir.getName().startsWith(username)) {
                    // 返回第一个匹配的主目录
                    return new File(dir, "cif");
                }
            }
        }

        return null; // 未找到匹配的目录
    }
}


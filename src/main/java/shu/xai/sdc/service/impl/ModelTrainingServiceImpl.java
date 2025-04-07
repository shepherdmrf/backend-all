package shu.xai.sdc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.sdc.entity.ModelData;
import shu.xai.sdc.mapper.ModelTrainingMapper;
import shu.xai.sdc.service.ModelTrainingService;

import java.io.*;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


@Service
public class ModelTrainingServiceImpl implements ModelTrainingService {

    private final ModelTrainingMapper modelTrainingMapper;
    private static final String TEMP_DIR = System.getProperty("user.dir") + "/temp";

    public ModelTrainingServiceImpl(ModelTrainingMapper modelTrainingMapper) {
        this.modelTrainingMapper = modelTrainingMapper;
    }

    @Override
    public List<ModelData> getAllModels() {
        return modelTrainingMapper.getAllModels();
    }

    @Override
    public ModelData getModelById(Integer id) {
        return modelTrainingMapper.getModelById(id);
    }

    @Override
    public void addModel(ModelData modelData) throws Exception {
        // 将模型数据存入数据库
        modelTrainingMapper.insert(modelData);

        // 获取 `models` 目录路径
        String baseDir = System.getProperty("user.dir");
        String modelsPath = Paths.get(baseDir, "models", modelData.getName()).toString();

        // 获取 `models` 代码
        String pythonDir = Paths.get(new File(baseDir).getParent(), "python_service").toString();

        // 确保 `models` 和 `python_service/Model_xxx` 目录都存在
        Files.createDirectories(Paths.get(modelsPath));
        Files.createDirectories(Paths.get(pythonDir));


        // 处理简介和架构文件
        moveFile(modelData.getIntroductionFile(), modelsPath);
        moveFile(modelData.getArchitectureFile(), modelsPath);

        // 处理 `zip` 模型文件
        String zipFilePath = Paths.get(TEMP_DIR, modelData.getName() + ".zip").toString();
        unzipFile(zipFilePath, pythonDir);
    }

    private void moveFile(String fileName, String destinationDir) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        Path sourcePath = Paths.get(TEMP_DIR, fileName);
        Path destinationPath = Paths.get(destinationDir, fileName);

        if (Files.exists(sourcePath)) {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void unzipFile(String zipFilePath, String extractDir) throws IOException {
        File destDir = new File(extractDir);
        if (!destDir.exists()) {
            Files.createDirectories(destDir.toPath());
        }

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File newFile = new File(extractDir, entry.getName());

                // 防止 Zip 路径穿越攻击
                String canonicalDestPath = newFile.getCanonicalPath();
                if (!canonicalDestPath.startsWith(destDir.getCanonicalPath())) {
                    throw new IOException("不安全的 ZipEntry 路径: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(newFile.toPath());
                } else {
                    // 确保父目录存在
                    Files.createDirectories(newFile.getParentFile().toPath());

                    // 复制文件内容
                    try (InputStream is = zipFile.getInputStream(entry);
                         OutputStream os = new BufferedOutputStream(new FileOutputStream(newFile))) {
                        byte[] buffer = new byte[8192]; // 8KB 缓冲区
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }

        // 解压完成后，删除 ZIP 文件
        Files.deleteIfExists(Paths.get(zipFilePath));
    }

    @Override
    public void clearTempDir() {
        File tempDir = new File(TEMP_DIR);
        if (tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void deleteModelAndFiles(Integer id, String username) throws Exception {
        // 从数据库中获取模型数据
        ModelData modelData = modelTrainingMapper.getModelById(id);
        if (modelData == null) {
            throw new IllegalArgumentException("模型不存在: id=" + id);
        }

        // 删除数据库中的模型
        modelTrainingMapper.deleteById(id);

        // 删除模型对应的文件夹
        String currentDirectory = System.getProperty("user.dir");
        Path modelFolderPath = Paths.get(currentDirectory, modelData.getModelPath());
        if (Files.exists(modelFolderPath)) {
            deleteDirectoryRecursively(modelFolderPath);
        }

        // 删除预测结果文件
        String modelName = modelData.getName(); // 获取模型名称
        Path resultsFilePath = Paths.get(currentDirectory, "results", username, modelName + "_results.csv");
        if (Files.exists(resultsFilePath)) {
            Files.delete(resultsFilePath);
        }

        // 删除 python_service 目录下的 Model_模型名称 文件夹
        Path pythonServicePath = Paths.get(currentDirectory).getParent().resolve("python_service");
        Path modelServiceFolderPath = pythonServicePath.resolve("Model_" + modelName);
        if (Files.exists(modelServiceFolderPath)) {
            deleteDirectoryRecursively(modelServiceFolderPath);
        }
    }

    // 递归删除文件夹
    private void deleteDirectoryRecursively(Path path) throws Exception {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursively(entry);
                }
            }
        }
        Files.delete(path); // 删除文件或空文件夹
    }

    @Override
    public String getModelIntroduction(String modelPath, String introductionFile) throws Exception {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, modelPath, introductionFile);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("文件不存在: " + filePath.toString());
        }
        return String.join("\n", Files.readAllLines(filePath));
    }

    @Override
    public byte[] getModelArchitecture(String modelPath, String architectureFile) throws Exception {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, modelPath, architectureFile);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("文件不存在: " + filePath.toString());
        }
        return Files.readAllBytes(filePath);
    }

    @Override
    public void uploadFile(MultipartFile file, String type) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空，上传失败");
        }

        // 检查文件类型是否合法
        validateFileType(file, type);

        // 确保 temp 目录存在
        Path tempFolderPath = Paths.get(TEMP_DIR);
        if (!Files.exists(tempFolderPath)) {
            Files.createDirectories(tempFolderPath);
        }

        // 保存文件，添加前缀区分
        Path filePath = tempFolderPath.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
    }

    private void validateFileType(MultipartFile file, String type) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("无法识别文件名");
        }

        String lowerCaseFileName = fileName.toLowerCase();
        switch (type) {
            case "model":
                if (!lowerCaseFileName.endsWith(".zip")) {
                    throw new IllegalArgumentException("模型文件必须是 .zip 格式");
                }
                break;
            case "architecture":
                if (!(lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg"))) {
                    throw new IllegalArgumentException("架构文件必须是 .png, .jpg, .jpeg 格式");
                }
                break;
            case "description":
                if (!lowerCaseFileName.endsWith(".txt")) {
                    throw new IllegalArgumentException("描述文件必须是 .txt 格式");
                }
                break;
            default:
                throw new IllegalArgumentException("未知文件类型");
        }
    }
}

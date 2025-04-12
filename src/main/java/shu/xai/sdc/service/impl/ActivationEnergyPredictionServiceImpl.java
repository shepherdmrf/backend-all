package shu.xai.sdc.service.impl;

import org.springframework.stereotype.Service;
import shu.xai.sdc.service.ActivationEnergyPredictionService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ActivationEnergyPredictionServiceImpl implements ActivationEnergyPredictionService {

    @Override
    public List<Map<String, Object>> predictActivationEnergy(String userName, List<String> filePaths, String modelName) throws Exception {
        // Step 1: 定义路径
        String baseDir = new File(System.getProperty("user.dir")).getParent();

        // 动态生成 pythonDir，根据前端传来的 modelName 选择对应模型文件夹
        String pythonDir = Paths.get(baseDir, "python_service", "Model_" + modelName).toString();

        // 打印 pythonDir 到控制台
        System.out.println("Python Directory: " + pythonDir);

        // 检查模型文件夹是否存在
        File modelDir = new File(pythonDir);
        if (!modelDir.exists() || !modelDir.isDirectory()) {
            throw new IllegalArgumentException("Model directory not found: " + pythonDir);
        }

        String datasetDir;
        String targetsFilePath;
        String resultsFilePath;

        // 根据 modelName 设置路径
        if ("SLIGNN".equals(modelName)) {
            datasetDir = Paths.get(pythonDir, "data", "dataset", userName + "-dataset").toString();
            targetsFilePath = Paths.get(pythonDir, "data", "dataset", "targets", userName + "-targets.csv").toString();
            resultsFilePath = Paths.get(pythonDir, "results", "regression", "test_results.csv").toString();
            Files.createDirectories(Paths.get(datasetDir));
        } else {
            datasetDir = Paths.get(pythonDir, "data", userName + "-dataset").toString();
            resultsFilePath = Paths.get(pythonDir, "mid_out", "test_results.csv").toString();
            targetsFilePath = Paths.get(pythonDir, "data", userName + "-dataset", "new_all_id_prop.csv").toString();
            Files.createDirectories(Paths.get(datasetDir));

            // 复制 JSON 文件到 datasetDir
            File cifDir = new File(Paths.get(pythonDir, "data", "cif").toString());
            File atomInitJson = new File(cifDir, "atom_init.json");
            Files.copy(atomInitJson.toPath(), Paths.get(datasetDir, "atom_init.json"), StandardCopyOption.REPLACE_EXISTING);
            if ("MEGNET".equals(modelName)) {
                File elementalEmbeddingJson = new File(cifDir, "elemental_embedding_1MEGNet_layer.json");
                Files.copy(elementalEmbeddingJson.toPath(), Paths.get(datasetDir, "elemental_embedding_1MEGNet_layer.json"), StandardCopyOption.REPLACE_EXISTING);
            }
        }

        // 输出目录路径
        String outputDir = Paths.get(System.getProperty("user.dir"), "results", userName).toString();
        Files.createDirectories(Paths.get(outputDir));

        // Step 2: 复制 CIF 文件
        for (String filePath : filePaths) {
            String newFilePath = datasetDir + "/" + Paths.get(filePath).getFileName().toString();
            Files.copy(Paths.get(filePath), Paths.get(newFilePath), StandardCopyOption.REPLACE_EXISTING);
        }

        // Step 3: 生成 targets.csv
        try (FileWriter writer = new FileWriter(targetsFilePath)) {
            for (String filePath : filePaths) {
                String fileName = Paths.get(filePath).getFileName().toString().replace(".cif", "");
                writer.write(fileName + ",0.0\n");
            }
        }

        // Step 4: 调用 Python 脚本
        ProcessBuilder pb;
        if ("SLIGNN".equals(modelName)) {
            pb = new ProcessBuilder(
                    "cmd.exe", "/c",
                    "conda run -n SLI-GNN python test.py model_best.pth.tar " +
                            userName + "-dataset" + " " + userName + "-targets"
            );
        } else {
            pb = new ProcessBuilder(
                    "cmd.exe", "/c",
                    "conda run -n SLI-GNN python test.py --dataset \"" + datasetDir + "\" --output_path \"" + resultsFilePath + "\""
            );
        }

        pb.directory(new File(pythonDir));
        pb.redirectErrorStream(true);
        pb.environment().put("PYTHONIOENCODING", "utf-8");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script execution failed with exit code " + exitCode);
        }

        // Step 5: 移动结果文件
        String outputFilePath = outputDir + "/" + modelName + "_results.csv";
        File existingResultsFile = new File(outputFilePath);

        // 新结果解析
        List<Map<String, Object>> newResults = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(resultsFilePath))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Map<String, Object> result = new HashMap<>();
                result.put("material_id", parts[0]);
                double prediction = Double.parseDouble(parts[2]);
                result.put("original_value", Math.pow(10, prediction));
                result.put("prediction", prediction);
                newResults.add(result);
            }
        }

        // Step 6: 合并新旧结果
        List<Map<String, Object>> finalResults = new ArrayList<>();
        if (existingResultsFile.exists()) {
            // 加载已有结果
            try (BufferedReader reader = new BufferedReader(new FileReader(existingResultsFile))) {
                String line = reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Map<String, Object> result = new HashMap<>();
                    result.put("material_id", parts[0]);
                    double prediction = Double.parseDouble(parts[2]);
                    result.put("original_value", Math.pow(10, prediction));
                    result.put("prediction", prediction);
                    finalResults.add(result);
                }
            }
        }

        // 合并新旧结果（按 material_id 去重）
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        for (Map<String, Object> result : finalResults) {
            resultMap.put((String) result.get("material_id"), result);
        }
        for (Map<String, Object> result : newResults) {
            resultMap.put((String) result.get("material_id"), result);
        }

        // 将合并结果写回文件
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write("material_id,original_value,prediction\n");
            for (Map<String, Object> result : resultMap.values()) {
                writer.write(result.get("material_id") + "," + result.get("original_value") + "," + result.get("prediction") + "\n");
            }
        }

        // 返回最终结果
        return new ArrayList<>(resultMap.values());
    }

    @Override
    public List<Map<String, Object>> getPredictionResults(String username, String modelName) {
        try {
            String resultsFilePath = System.getProperty("user.dir") + "/results/" + username + "/" + modelName + "_results.csv";

            File resultsFile = new File(resultsFilePath);
            if (!resultsFile.exists()) {
                throw new FileNotFoundException("Results file not found for model: " + modelName);
            }

            List<Map<String, Object>> results = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(resultsFile))) {
                String line = reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Map<String, Object> result = new HashMap<>();
                    result.put("material_id", parts[0] + ".cif");
                    result.put("original_value", Double.parseDouble(parts[1])); // 原值
                    results.add(result);
                }
            }
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve prediction results", e);
        }
    }

    @Override
    public File getResultsFile(String userName, String modelName) throws Exception {
        // 动态生成文件路径
        String resultsFilePath = System.getProperty("user.dir") + "/results/" + userName + "/" + modelName + "_results.csv";

        // 检查文件是否存在
        File resultsFile = new File(resultsFilePath);
        if (!resultsFile.exists()) {
            throw new FileNotFoundException("Results file not found for model: " + modelName);
        }

        return resultsFile;
    }
}

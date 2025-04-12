package shu.xai.sdc.service.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import shu.xai.sdc.entity.Element;
import shu.xai.sdc.mapper.ElementMapper;
import shu.xai.sdc.service.CrystalStructureRepresentationService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CrystalStructureRepresentationServiceImpl implements CrystalStructureRepresentationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String pythonServiceUrl = "http://localhost:5000/crystal_structure"; // Python 服务接口
    private final String baseSaveDirectory = System.getProperty("user.dir");
    private final String baseDir = new File(System.getProperty("user.dir")).getParent();

    @Autowired
    private ElementMapper elementMapper;

    @Override
    public List<Element> getAllElements() {
        return elementMapper.getAllElements();
    }

    @Override
    public Map<String, String> convertFiles(List<String> filePaths, String userName, Double radius, Integer max_num_nbr) {
        Map<String, String> conversionResults = new HashMap<>();
        String saveDirectory = Paths.get(baseSaveDirectory, "Crystal_Structure_Result", userName).toString();

        for (String filePath : filePaths) {
            try {
                // 调用 Python 服务
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("cif_path", filePath);
                requestBody.put("save_directory", saveDirectory);
                requestBody.put("radius", radius);
                requestBody.put("max_num_nbr", max_num_nbr);

                // 发起 POST 请求
                ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl, requestBody, Map.class);

                // 处理 Python 服务响应
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map responseBody = response.getBody();
                    Map<String, String> result = (Map<String, String>) responseBody.get("result");

                    if (result != null) {
                        conversionResults.put(filePath, "转换成功");
                    } else {
                        conversionResults.put(filePath, "转换失败：Python 服务未返回 result 字段");
                    }
                } else {
                    conversionResults.put(filePath, "转换失败：Python 服务响应异常");
                }
            } catch (Exception e) {
                conversionResults.put(filePath, "转换失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
        return conversionResults;
    }

    @Override
    public String getCifContent(String filePath, String fileNames, String username) throws IOException {
        // 获取原始 CIF 文件路径
        Path originalCifPath = Paths.get(filePath);

        // 检查文件是否存在
        if (!Files.exists(originalCifPath)) {
            throw new IOException("原始 CIF 文件不存在: " + originalCifPath.toString());
        }

        // 计算转换后的 CIF 文件路径
        Path convertedCifFilePath = Paths.get(baseSaveDirectory, "Crystal_Structure_Result", username, fileNames.replace(".cif", "_converted.cif"));

        // 如果转换后的 CIF 文件已经存在，直接返回其内容
        if (Files.exists(convertedCifFilePath)) {
            System.out.println("转换后的 CIF 文件已存在，直接读取并返回内容");
            String cifContent = new String(Files.readAllBytes(convertedCifFilePath), StandardCharsets.UTF_8);
            return cifContent;
        }

        // 构建 Python 脚本路径
        String pythonScriptPath = Paths.get(baseDir, "python_service", "Cif_Convert", "expand_cif_structure.py").toString();

        // 获取 Python 脚本所在目录
        File pythonScriptDirectory = new File(pythonScriptPath).getParentFile();

        // 调用 Python 脚本进行转换
        ProcessBuilder processBuilder = new ProcessBuilder("python.exe", pythonScriptPath, originalCifPath.toString(), convertedCifFilePath.toString());

        // 设置工作目录为 Python 脚本所在的目录
        processBuilder.directory(pythonScriptDirectory);

        // 捕获标准输出和标准错误输出
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 获取 Python 脚本的输出流
        InputStream inputStream = process.getInputStream();
        StringBuilder output = new StringBuilder();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            output.append((char) ch);
        }

        // 异常处理：如果进程没有正常结束
        int exitCode = process.isAlive() ? 1 : 0;  // 这里检查进程是否仍然存活
        System.out.println("Python 脚本输出: " + output.toString());


        if (exitCode == 0) {
            // 转换成功，读取转换后的 CIF 文件内容
            if (Files.exists(convertedCifFilePath)) {
                String cifContent = new String(Files.readAllBytes(convertedCifFilePath), StandardCharsets.UTF_8);
                return cifContent;  // 返回 CIF 文件内容
            } else {
                throw new IOException("转换后的 CIF 文件不存在: " + convertedCifFilePath.toString());
            }
        } else {
            // 如果脚本失败，打印错误并抛出异常
            System.err.println("Python 脚本执行失败，退出码: " + exitCode);
            System.err.println("Python 错误输出: " + output.toString());
            throw new IOException("Python 脚本执行失败，退出码: " + exitCode);
        }
    }

    @Override
    public Map<String, Object> previewFile(String userName, String fileName) throws IOException {
        // 定义保存路径
        String saveDirectory = Paths.get(baseSaveDirectory, "Crystal_Structure_Result", userName).toString();
        String baseFileName = fileName.replace(".cif", "");
        String edgesFilePath = Paths.get(saveDirectory, baseFileName + "_edges.txt").toString();
        String nodesFilePath = Paths.get(saveDirectory, baseFileName + "_nodes.txt").toString();

        Map<String, Object> fileData = new HashMap<>();

        // 读取 edges 文件内容并统计信息
        if (Files.exists(Paths.get(edgesFilePath))) {
            String edgesContent = new String(Files.readAllBytes(Paths.get(edgesFilePath)), StandardCharsets.UTF_8);
            fileData.put("edges", edgesContent);

            // 提取边文件统计信息
            fileData.put("edgeStats", calculateEdgeStats(edgesContent));
        } else {
            fileData.put("edges", "文件未找到：" + edgesFilePath);
            System.err.println("Edges file not found: " + edgesFilePath);
        }

        // 读取 nodes 文件内容并统计信息
        if (Files.exists(Paths.get(nodesFilePath))) {
            String nodesContent = new String(Files.readAllBytes(Paths.get(nodesFilePath)), StandardCharsets.UTF_8);
            fileData.put("nodes", nodesContent);

            // 提取节点文件统计信息
            fileData.put("nodeStats", calculateNodeStats(nodesContent));
        } else {
            fileData.put("nodes", "文件未找到：" + nodesFilePath);
            System.err.println("Nodes file not found: " + nodesFilePath);
        }

        return fileData;
    }

    /**
     * 计算边文件的统计信息
     */
    private Map<String, Object> calculateEdgeStats(String edgesContent) {
        Map<String, Object> stats = new HashMap<>();
        String[] lines = edgesContent.split("\n");

        stats.put("edgeCount", lines.length);

        // 高斯反变换参数
        double dmin = 0.0;  // 最小距离
        double dmax = 8.0;  // 最大距离
        int steps = 100;    // 高斯滤波器步长
        double[] filter = new double[steps];
        for (int i = 0; i < steps; i++) {
            filter[i] = dmin + i * (dmax - dmin) / (steps - 1);
        }

        double minEdgeLength = Double.MAX_VALUE;
        double maxEdgeLength = Double.MIN_VALUE;

        Map<Integer, Integer> degreeDistribution = new HashMap<>();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\)\\t");
                if (parts.length == 2) {
                    String edgeInfo = parts[1].trim();
                    String[] features = edgeInfo.split(" ");  // 高斯特征值分隔符
                    if (features.length == filter.length) {
                        // 反变换：找到特征最大值对应的索引
                        int maxIndex = 0;
                        double maxValue = Double.MIN_VALUE;
                        for (int i = 0; i < features.length; i++) {
                            double value = Double.parseDouble(features[i]);
                            if (value > maxValue) {
                                maxValue = value;
                                maxIndex = i;
                            }
                        }
                        // 恢复原始边长
                        double edgeLength = filter[maxIndex];
                        minEdgeLength = Math.min(minEdgeLength, edgeLength);
                        maxEdgeLength = Math.max(maxEdgeLength, edgeLength);
                    }

                    // 计算节点度分布
                    String[] nodes = parts[0].replace("(", "").replace(")", "").split(", ");
                    for (String node : nodes) {
                        int nodeId = Integer.parseInt(node.trim());
                        degreeDistribution.put(nodeId, degreeDistribution.getOrDefault(nodeId, 0) + 1);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("解析边文件时出错，跳过一行：" + line);
            }
        }

        stats.put("minEdgeLength", minEdgeLength == Double.MAX_VALUE ? 0 : minEdgeLength);
        stats.put("maxEdgeLength", maxEdgeLength == Double.MIN_VALUE ? 0 : maxEdgeLength);
        stats.put("degreeDistribution", degreeDistribution);

        return stats;
    }

    /**
     * 计算节点文件的统计信息
     */
    private Map<String, Object> calculateNodeStats(String nodesContent) {
        Map<String, Object> stats = new HashMap<>();
        String[] lines = nodesContent.split("\n");

        stats.put("nodeCount", lines.length);

        Map<Integer, Integer> atomTypeDistribution = new HashMap<>();
        int featureDimension = 0;

        for (String line : lines) {
            try {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    int atomType = Integer.parseInt(parts[1].trim());
                    atomTypeDistribution.put(atomType, atomTypeDistribution.getOrDefault(atomType, 0) + 1);

                    // 获取特征向量维度
                    featureDimension = parts[2].split(" ").length;
                }
            } catch (NumberFormatException e) {
                System.err.println("解析节点文件内容时失败，跳过一行：" + line);
                continue;
            }
        }

        stats.put("atomTypeDistribution", atomTypeDistribution);
        stats.put("featureDimension", featureDimension);

        return stats;
    }

    @Override
    public byte[] downloadFiles(String userName, List<String> fileNames) throws IOException {
        String saveDirectory = Paths.get(baseSaveDirectory, "Crystal_Structure_Result", userName).toString();
        List<File> filesToZip = new ArrayList<>();

        // 遍历文件名列表，添加对应的 edges 和 nodes 文件到列表
        for (String fileName : fileNames) {
            String baseFileName = fileName.replace(".cif", "");
            String edgesFilePath = Paths.get(saveDirectory, baseFileName + "_edges.txt").toString();
            String nodesFilePath = Paths.get(saveDirectory, baseFileName + "_nodes.txt").toString();

            File edgesFile = new File(edgesFilePath);
            File nodesFile = new File(nodesFilePath);

            if (edgesFile.exists()) {
                filesToZip.add(edgesFile);
            } else {
                System.err.println("Edges file not found: " + edgesFilePath);
            }

            if (nodesFile.exists()) {
                filesToZip.add(nodesFile);
            } else {
                System.err.println("Nodes file not found: " + nodesFilePath);
            }
        }

        // 将文件压缩为 ZIP 并返回字节数组
        return createZipFile(filesToZip);
    }

    /**
     * 将文件列表压缩为 ZIP 并返回字节数组
     */
    private byte[] createZipFile(List<File> files) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    zipOutputStream.closeEntry();
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}

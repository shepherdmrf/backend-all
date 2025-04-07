package shu.xai.sdc.service.impl;

import cn.hutool.json.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import shu.xai.sdc.service.InterstitialNetworkRepresentationService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class InterstitialNetworkRepresentationServiceImpl implements InterstitialNetworkRepresentationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String pythonServiceUrl = "http://localhost:5001/interstice-network"; // Python 服务地址
    private final String baseSaveDirectory = System.getProperty("user.dir");
    private final String atomFeatureFileName = "atom_feature.csv"; // atom_feature.csv 文件名
    private final String baseDir = new File(System.getProperty("user.dir")).getParent();

    @Override
    public Map<String, String> convertFiles(List<String> filePaths, String userName, String migrantIon) throws Exception {
        Map<String, String> conversionResults = new HashMap<>();
        String saveDirectory = Paths.get(baseSaveDirectory, "Interstitial_Network_Results", userName).toString();

        // 确保保存目录存在
        Path saveDirectoryPath = Paths.get(saveDirectory);
        if (!Files.exists(saveDirectoryPath)) {
            Files.createDirectories(saveDirectoryPath);
        }

        // 复制 atom_feature.csv 到目标目录
        Path atomFeatureSource = Paths.get(baseSaveDirectory, "src", "main", "resources", "static", atomFeatureFileName);
        Path atomFeatureDestination = Paths.get(saveDirectory, atomFeatureFileName);

        try {
            Files.copy(atomFeatureSource, atomFeatureDestination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("atom_feature.csv successfully copied to " + atomFeatureDestination);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy atom_feature.csv: " + e.getMessage(), e);
        }

        for (String filePath : filePaths) {
            try {
                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("cif_path", filePath);
                requestBody.put("save_directory", saveDirectory);
                requestBody.put("migrant_ion",  migrantIon);

                // 调用 Python 服务
                ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl, requestBody, Map.class);

                // 检查 Python 服务的响应
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map responseBody = response.getBody();
                    String status = (String) responseBody.get("status");

                    if ("success".equals(status)) {
                        conversionResults.put(filePath, "转换成功");
                    } else {
                        conversionResults.put(filePath, "转换失败：" + responseBody.get("message"));
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
    public Map<String, String> getCifContentandSpt(String fileNames, String username) throws IOException {
        Path userDir = Paths.get(baseSaveDirectory, "Interstitial_Network_Results", username);
        Path vestaFilePath = userDir.resolve(fileNames.replace(".cif", ".vesta"));
        Path convertedCifFilePath = userDir.resolve(fileNames.replace(".cif", "_converted.cif"));
        Path spacefillScriptPath = userDir.resolve(fileNames.replace(".cif", "_converted_spacefill.spt"));

        if (!Files.exists(vestaFilePath)) {
            throw new IOException("VESTA 文件不存在: " + vestaFilePath.toString());
        }

        // 如果 CIF 已存在，直接读取 CIF 和 .spt 内容
        if (Files.exists(convertedCifFilePath)) {
            System.out.println("转换后的 CIF 文件已存在，直接读取并返回内容");

            Map<String, String> result = new HashMap<>();
            result.put("networkCifContent", new String(Files.readAllBytes(convertedCifFilePath), StandardCharsets.UTF_8));
            if (Files.exists(spacefillScriptPath)) {
                result.put("sptContent", new String(Files.readAllBytes(spacefillScriptPath), StandardCharsets.UTF_8));
            } else {
                result.put("sptContent", ""); // 防止前端空指针
            }
            return result;
        }


        // 构建 Python 脚本路径
        String pythonScriptPath = Paths.get(baseDir, "python_service", "Cif_Convert", "convert_vesta_net_to_cif.py").toString();
        // 获取 Python 脚本所在目录
        File pythonScriptDirectory = new File(pythonScriptPath).getParentFile();

        // 调用 Python 脚本进行转换
        ProcessBuilder processBuilder = new ProcessBuilder("C:\\Program Files\\Python311\\python.exe", pythonScriptPath, vestaFilePath.toString());

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
        int exitCode = process.isAlive() ? 1 : 0;

        if (exitCode == 0) {
            if (Files.exists(convertedCifFilePath)) {
                Map<String, String> result = new HashMap<>();
                result.put("networkCifContent", new String(Files.readAllBytes(convertedCifFilePath), StandardCharsets.UTF_8));
                result.put("sptContent", Files.exists(spacefillScriptPath)
                        ? new String(Files.readAllBytes(spacefillScriptPath), StandardCharsets.UTF_8)
                        : "");
                return result;
            } else {
                throw new IOException("转换后的 CIF 文件不存在: " + convertedCifFilePath.toString());
            }
        } else {
            System.err.println("Python 脚本执行失败，退出码: " + exitCode);
            System.err.println("Python 错误输出: " + output.toString());
            throw new IOException("Python 脚本执行失败，退出码: " + exitCode);
        }
    }

    @Override
    public Map<String, Object> previewFile(String userName, String fileName) throws IOException {
        // 定义保存路径
        String saveDirectory = Paths.get(baseSaveDirectory, "Interstitial_Network_Results", userName).toString();
        // 构造文件路径
        String baseFileName = fileName.replace(".cif", "");
        String voidAtomsFilePath = Paths.get(saveDirectory, baseFileName + "_adjacency_void_atoms2.txt").toString();
        String channelAtomsFilePath = Paths.get(saveDirectory, baseFileName + "_adjacency_channel_atoms2.txt").toString();

        Map<String, Object> fileData = new HashMap<>();

        // 读取 _adjacency_void_atoms2.txt 文件内容
        if (Files.exists(Paths.get(voidAtomsFilePath))) {
            String voidAtomsContent = new String(Files.readAllBytes(Paths.get(voidAtomsFilePath)), StandardCharsets.UTF_8);
            fileData.put("adjacency_void_atoms2", voidAtomsContent);
            fileData.put("void_stats", calculateVoidStats(voidAtomsContent));
        } else {
            fileData.put("adjacency_void_atoms2", "文件未找到：" + voidAtomsFilePath);
            System.err.println("Void adjacency file not found: " + voidAtomsFilePath);
        }

        // 读取 _adjacency_channel_atoms2.txt 文件内容
        if (Files.exists(Paths.get(channelAtomsFilePath))) {
            String channelAtomsContent = new String(Files.readAllBytes(Paths.get(channelAtomsFilePath)), StandardCharsets.UTF_8);
            fileData.put("adjacency_channel_atoms2", channelAtomsContent);
            fileData.put("channel_stats", calculateChannelStats(channelAtomsContent));
        } else {
            fileData.put("adjacency_channel_atoms2", "文件未找到：" + channelAtomsFilePath);
            System.err.println("Channel adjacency file not found: " + channelAtomsFilePath);
        }

        return fileData;
    }

    private Map<String, Object> calculateVoidStats(String content) {
        Map<String, Object> stats = new HashMap<>();
        String[] lines = content.split("\\n");
        int totalNodes = lines.length;
        double[] radii = Arrays.stream(lines)
                .mapToDouble(this::extractRadiusFromVoidLine)
                .toArray();

        stats.put("totalNodes", totalNodes);
        stats.put("minVoidRadius", Arrays.stream(radii).min().orElse(0.0));
        stats.put("maxVoidRadius", Arrays.stream(radii).max().orElse(0.0));
        stats.put("avgVoidRadius", Arrays.stream(radii).average().orElse(0.0));
        return stats;
    }

    private double extractRadiusFromVoidLine(String line) {
        String[] parts = line.trim().split("\\s+");
        return parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0; // 假设半径在第三列
    }

    private Map<String, Object> calculateChannelStats(String content) {
        Map<String, Object> stats = new HashMap<>();
        String[] lines = content.split("\\n");
        int totalEdges = lines.length;
        double[] radii = Arrays.stream(lines)
                .mapToDouble(this::extractRadiusFromChannelLine)
                .toArray();

        stats.put("totalEdges", totalEdges);
        stats.put("minBottleneckRadius", Arrays.stream(radii).min().orElse(0.0));
        stats.put("maxBottleneckRadius", Arrays.stream(radii).max().orElse(0.0));
        stats.put("avgBottleneckRadius", Arrays.stream(radii).average().orElse(0.0));
        return stats;
    }

    private double extractRadiusFromChannelLine(String line) {
        String[] parts = line.trim().split("\\s+");
        return parts.length > 3 ? Double.parseDouble(parts[3]) : 0.0; // 假设半径在第四列
    }

    @Override
    public byte[] downloadFiles(String userName, List<String> fileNames) throws IOException {
        String saveDirectory = Paths.get(baseSaveDirectory, "Interstitial_Network_Results", userName).toString();
        List<File> filesToZip = new ArrayList<>();

        // 遍历文件名列表，添加对应的文件到列表
        for (String fileName : fileNames) {
            String baseFileName = fileName.replace(".cif", "");

            // 添加 _adjacency_channel_atoms2.txt 文件
            String channelFilePath = Paths.get(saveDirectory, baseFileName + "_adjacency_channel_atoms2.txt").toString();
            File channelFile = new File(channelFilePath);
            if (channelFile.exists()) {
                filesToZip.add(channelFile);
            } else {
                System.err.println("Channel adjacency file not found: " + channelFilePath);
            }

            // 添加 _adjacency_void_atoms2.txt 文件
            String voidFilePath = Paths.get(saveDirectory, baseFileName + "_adjacency_void_atoms2.txt").toString();
            File voidFile = new File(voidFilePath);
            if (voidFile.exists()) {
                filesToZip.add(voidFile);
            } else {
                System.err.println("Void adjacency file not found: " + voidFilePath);
            }

            // 添加 .net 文件
            String netFilePath = Paths.get(saveDirectory, baseFileName + ".net").toString();
            File netFile = new File(netFilePath);
            if (netFile.exists()) {
                filesToZip.add(netFile);
            } else {
                System.err.println("Net file not found: " + netFilePath);
            }

            // 添加 .vesta 文件
            String vestaFilePath = Paths.get(saveDirectory, baseFileName + ".vesta").toString();
            File vestaFile = new File(vestaFilePath);
            if (vestaFile.exists()) {
                filesToZip.add(vestaFile);
            } else {
                System.err.println("Vesta file not found: " + vestaFilePath);
            }

            // 添加 _origin.net 文件
            String originNetFilePath = Paths.get(saveDirectory, baseFileName + "_origin.net").toString();
            File originNetFile = new File(originNetFilePath);
            if (originNetFile.exists()) {
                filesToZip.add(originNetFile);
            } else {
                System.err.println("Origin net file not found: " + originNetFilePath);
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

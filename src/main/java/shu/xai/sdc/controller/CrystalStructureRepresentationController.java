package shu.xai.sdc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shu.xai.sdc.entity.Element;
import shu.xai.sdc.service.CrystalStructureRepresentationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/crystal_structure")
public class CrystalStructureRepresentationController {

    @Autowired
    private CrystalStructureRepresentationService crystalStructureService;

    /**
     * 接收文件路径和用户名，触发转换逻辑
     */
    @PostMapping("/convert")
    public ResponseEntity<?> convertFiles(@RequestBody Map<String, Object> payload) {
        try {
            // 从请求中提取文件路径和用户名和其他参数
            List<String> filePaths = (List<String>) payload.get("file_paths");
            String userName = (String) payload.get("user_name");
            Double radius = payload.get("radius") != null
                    ? Double.parseDouble(payload.get("radius").toString())
                    : null;
            Integer max_num_nbr = payload.get("max_num_nbr") != null
                    ? Integer.parseInt(payload.get("max_num_nbr").toString())
                    : null;

            // 打印接收到的参数
            System.out.println("Received radius: " + radius);
            System.out.println("Received maxNumNbr: " + max_num_nbr);

            // 检查参数是否有效
            if (filePaths == null || filePaths.isEmpty()) {
                return ResponseEntity.badRequest().body("文件路径不能为空！");
            }
            if (userName == null || userName.isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空！");
            }
            if (radius == null || radius <= 0) {
                return ResponseEntity.badRequest().body("截断距离必须为正数！");
            }
            if (max_num_nbr == null || max_num_nbr <= 0) {
                return ResponseEntity.badRequest().body("邻近原子数必须为正整数！");
            }

            // 调用 Service 方法执行转换
            Map<String, String> conversionResults = crystalStructureService.convertFiles(filePaths, userName, radius, max_num_nbr);

            // 返回转换结果
            return ResponseEntity.ok(conversionResults);
        } catch (Exception e) {
            // 捕获异常并返回 500 状态码
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    @PostMapping("/getCifContent")
    public ResponseEntity<?> getCifContent(@RequestBody Map<String, String> requestData) {
        String filePath = requestData.get("filePath");
        String fileNames = (String) requestData.get("fileNames");
        String username = (String) requestData.get("username");

        try {
            // 调用 Service 层来读取 CIF 文件并获取内容
            String cifContent = crystalStructureService.getCifContent(filePath, fileNames, username);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("cifContent", cifContent);
            }});
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("无法读取文件内容");
        }
    }

    /**
     * 获取所有元素数据
     *
     * @return 元素列表
     */
    @GetMapping("/elements")
    public List<Element> getAllElements() {
        return crystalStructureService.getAllElements();
    }

    /**
     * 文件预览功能，接收用户名和文件名，返回内容
     */
    @GetMapping("/filePreview")
    public ResponseEntity<?> previewFile(
            @RequestParam("userName") String userName,
            @RequestParam("fileName") String fileName
    ) {
        try {
            // 检查参数
            if (userName == null || userName.isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空！");
            }
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body("文件名不能为空！");
            }

            // 调用 Service 方法获取文件内容和统计信息
            Map<String, Object> fileData = crystalStructureService.previewFile(userName, fileName);

            return ResponseEntity.ok(fileData);
        } catch (Exception e) {
            System.err.println("文件预览失败：" + e.getMessage());
            return ResponseEntity.status(500).body("文件预览失败：" + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFiles(
            @RequestParam("userName") String userName,
            @RequestParam("fileNames") String fileNames) {
        try {
            // 打印接收到的参数
            System.out.println("Received userName: " + userName);
            System.out.println("Received fileNames: " + fileNames);

            // 将逗号分隔的字符串转换为文件名列表
            List<String> fileNameList = Arrays.asList(fileNames.split(","));

            // 调用服务层获取压缩文件数据
            byte[] zipData = crystalStructureService.downloadFiles(userName, fileNameList);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

            // 返回压缩包文件
            System.out.println("Download successful, returning ZIP file.");
            return new ResponseEntity<>(zipData, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during file download: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

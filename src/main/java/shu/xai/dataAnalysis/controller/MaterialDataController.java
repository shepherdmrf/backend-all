package shu.xai.dataAnalysis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.entity.PageResult;
import shu.xai.dataAnalysis.service.MaterialDataService;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class MaterialDataController {

    @Resource
    private MaterialDataService materialDataService;

    @GetMapping("/paginated")
    public  Map<String, Object> getPaginatedData(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ) {

        PageResult<Map<String, Object>> result = materialDataService.getPaginatedData(page, size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("List", result.getList());
        response.put("Total", result.getTotal());

        // 检查生成的 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(result);
            System.out.println("生成的 JSON: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return response;
    }

    @GetMapping("/statistics")
    public Map<String, Map<String, Object>> getStatistics() {
        return materialDataService.getStatistics();
    }

    @GetMapping("/violin")
    public ResponseEntity<org.springframework.core.io.Resource> getViolin() {
        try {
            // 调用服务层处理特征
            File generatedImage = materialDataService.generateViolin();

            if (generatedImage == null || !generatedImage.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new InputStreamResource(new ByteArrayInputStream("Failed to generate image".getBytes())));
            }

            // 读取生成的图片并返回
            InputStreamResource resource = new InputStreamResource(new FileInputStream(generatedImage));

            // 设置响应头和内容类型，返回图片
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + generatedImage.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputStreamResource(new ByteArrayInputStream("An error occurred while generating the image".getBytes())));
        }
    }
}


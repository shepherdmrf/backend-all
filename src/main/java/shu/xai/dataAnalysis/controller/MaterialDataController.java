package shu.xai.dataAnalysis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.entity.PageResult;
import shu.xai.dataAnalysis.service.MaterialDataService;

import javax.annotation.Resource;
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

        PageResult<MaterialData> result = materialDataService.getPaginatedData(page, size);

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
}


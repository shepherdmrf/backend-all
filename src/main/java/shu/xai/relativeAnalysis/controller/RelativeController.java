package shu.xai.relativeAnalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.relativeAnalysis.service.RelativeService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/relative")
public class RelativeController {

    @Autowired
    private RelativeService relativeService;

    @GetMapping("/svr")
    public List<Feature> getSVRCluster() {
        return relativeService.getSVRCluster();
    }

    @GetMapping("/getImageUrls")
    public ResponseEntity<Map<String, String>> getImageUrls(@RequestParam int Id) {
        try {
            // 获取高维和低维图片的 URL
            relativeService.generateImagesSvr(Id);

            String highDimensionalImageUrl = "/relative/highDimensionalImage";
            String lowDimensionalImageUrl = "/relative/lowDimensionalImage";

            // 返回图片 URL
            Map<String, String> imageUrls = new HashMap<>();
            imageUrls.put("highDimensionalImage", highDimensionalImageUrl);
            imageUrls.put("lowDimensionalImage", lowDimensionalImageUrl);

            return ResponseEntity.ok(imageUrls);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while generating the image URLs");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/highDimensionalImage")
    public ResponseEntity<Resource> getHighDimensionalImage() {
        try {
            // 获取高维图片文件
            File file = relativeService.getHighDimensionalImage();

            if (file == null || !file.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new InputStreamResource(new ByteArrayInputStream("Failed to generate image".getBytes())));
            }

            // 读取生成的图片并返回
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/lowDimensionalImage")
    public ResponseEntity<Resource> getLowDimensionalImage() {
        try {
            // 获取低维图片文件
            File file = relativeService.getLowDimensionalImage();

            if (file == null || !file.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new InputStreamResource(new ByteArrayInputStream("Failed to generate image".getBytes())));
            }

            // 读取生成的图片并返回
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }





}

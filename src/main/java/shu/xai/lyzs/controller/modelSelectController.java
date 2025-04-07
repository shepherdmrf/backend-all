package shu.xai.lyzs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.entity.scData;
import shu.xai.lyzs.service.modelSelectService;
import shu.xai.lyzs.service.scDataService;

import java.util.List;

@RestController
@RequestMapping("/Model")
public class modelSelectController {
    @Autowired(required=false)
    modelSelectService modelSelectService;
    @Autowired(required=false)
    scDataService scDataService;
    @GetMapping("/modelselect")
    public List<modelSelect> getAllExcelData(@RequestParam String category) {
        System.out.println("cate"+category);
        return modelSelectService.getData(category);
    }
    @GetMapping("/modelselectafter")
    public List<modelSelect> getExcelData(@RequestParam String category,@RequestParam String clusterAlgorithmValue) {
        return modelSelectService.getafterData(category,clusterAlgorithmValue);
    }
    @GetMapping("/sc")
    public List<scData> getsc(@RequestParam String category) {
        System.out.println("cate:"+category);
        return scDataService.getData(category);
    }
}

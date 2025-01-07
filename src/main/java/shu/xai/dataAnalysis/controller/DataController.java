package shu.xai.dataAnalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.service.DataService;

import javax.xml.crypto.Data;
import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private DataService dataService;

    @GetMapping("/getdata")
    public List<MaterialData> getAllData() {
        MaterialData materialData = new MaterialData();
        List<MaterialData> list = dataService.getAllData(materialData);
        return list;
    }
}


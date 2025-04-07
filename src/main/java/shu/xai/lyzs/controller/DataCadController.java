package shu.xai.lyzs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.lyzs.entity.DataCad;
import shu.xai.lyzs.entity.DataCfi;
import shu.xai.lyzs.service.DataCadService;
import shu.xai.lyzs.service.DataCfiService;

import java.util.List;

@RestController
@RequestMapping("/cad")
public class DataCadController {
    @Autowired(required = false)
    DataCadService dataCadService;

    @GetMapping("/CAD")
    public List<DataCad> getCad(@RequestParam String clusterAlgorithmValue){
        System.out.println("cad:"+clusterAlgorithmValue);
        return dataCadService.getCad(clusterAlgorithmValue);
    }

}

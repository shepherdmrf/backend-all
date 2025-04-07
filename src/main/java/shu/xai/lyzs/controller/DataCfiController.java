package shu.xai.lyzs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shu.xai.lyzs.entity.DataCfi;
import shu.xai.lyzs.service.DataCfiService;

import java.util.List;

@RestController
@RequestMapping("/cgse")
public class DataCfiController {
    @Autowired(required = false)
    DataCfiService dataCfiService;

    @GetMapping("/Expert4")
    public List<DataCfi> getExpert(){
        return dataCfiService.getExpert();
    }
    @GetMapping("/Expert1")
    public List<DataCfi> getCfi(){
        return dataCfiService.getCfi();
    }
    @GetMapping("/Expert2")
    public List<DataCfi> getGini(){
        return dataCfiService.getGini();
    }
    @GetMapping("/Expert3")
    public List<DataCfi> getSpearman(){
        return dataCfiService.getSpearman();
    }
}

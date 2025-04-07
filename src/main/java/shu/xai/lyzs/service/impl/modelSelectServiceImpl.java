package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.mapper.modelSelectMapper;
import shu.xai.lyzs.service.modelSelectService;

import java.util.List;

@Service
public class modelSelectServiceImpl  implements modelSelectService {
    @Autowired
    modelSelectMapper modelSelectMapper;
    public List<modelSelect> getData(String category){
        return modelSelectMapper.getData(category);
    }
    public List<modelSelect> getafterData(String category,String clusterAlgorithmValue){
        return modelSelectMapper.getafterData(category,clusterAlgorithmValue);
    }
}

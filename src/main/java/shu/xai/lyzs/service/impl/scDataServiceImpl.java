package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.entity.scData;
import shu.xai.lyzs.mapper.modelSelectMapper;
import shu.xai.lyzs.mapper.scDataMapper;
import shu.xai.lyzs.service.modelSelectService;
import shu.xai.lyzs.service.scDataService;

import java.util.List;

@Service
public class scDataServiceImpl implements scDataService {
    @Autowired
    scDataMapper scDataMapper;
    public List<scData> getData(String category){
        return scDataMapper.getData(category);
    }
}

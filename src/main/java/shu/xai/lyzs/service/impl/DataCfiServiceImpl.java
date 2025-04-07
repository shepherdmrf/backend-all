package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.DataCfi;
import shu.xai.lyzs.entity.scData;
import shu.xai.lyzs.mapper.DataCfiMapper;
import shu.xai.lyzs.mapper.scDataMapper;
import shu.xai.lyzs.service.DataCfiService;
import shu.xai.lyzs.service.scDataService;

import java.util.List;

@Service
public class DataCfiServiceImpl implements DataCfiService {
    @Autowired
    DataCfiMapper DataCfiMapper;
    public List<DataCfi> getExpert(){
        return DataCfiMapper.getExpert();
    }
    public List<DataCfi> getCfi(){
        return DataCfiMapper.getCfi();
    }
    public List<DataCfi> getGini(){
        return DataCfiMapper.getGini();
    }
    public List<DataCfi> getSpearman(){
        return DataCfiMapper.getSpearman();
    }
}

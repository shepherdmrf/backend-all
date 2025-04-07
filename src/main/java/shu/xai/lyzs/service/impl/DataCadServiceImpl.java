package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.DataCad;
import shu.xai.lyzs.entity.DataCfi;
import shu.xai.lyzs.mapper.DataCadMapper;
import shu.xai.lyzs.mapper.DataCfiMapper;
import shu.xai.lyzs.service.DataCadService;
import shu.xai.lyzs.service.DataCfiService;

import java.util.List;

@Service
public class DataCadServiceImpl implements DataCadService {
    @Autowired
    DataCadMapper  dataCadMapper;
    public List<DataCad> getCad(String clusterAlgorithmValue){

        return dataCadMapper.getCad(clusterAlgorithmValue);
    }

}

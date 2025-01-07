package shu.xai.dataAnalysis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.mapper.DataMapper;
import shu.xai.dataAnalysis.service.DataService;

import javax.xml.crypto.Data;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    DataMapper dataMapper;

    public List<MaterialData> getAllData(MaterialData materialData) {
        return dataMapper.getAllData(materialData);
    }
}

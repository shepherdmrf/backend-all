package shu.xai.dataAnalysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.mapper.DataMapper;

import javax.xml.crypto.Data;
import java.util.List;

public interface DataService {
    List<MaterialData> getAllData(MaterialData materialData);
}


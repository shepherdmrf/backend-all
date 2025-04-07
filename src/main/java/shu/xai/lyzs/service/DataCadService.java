package shu.xai.lyzs.service;

import org.apache.ibatis.annotations.Param;
import shu.xai.lyzs.entity.DataCad;
import shu.xai.lyzs.entity.DataCfi;

import java.util.List;

public interface DataCadService {
    List<DataCad> getCad(@Param("clusterAlgorithmValue")String clusterAlgorithmValue);

}

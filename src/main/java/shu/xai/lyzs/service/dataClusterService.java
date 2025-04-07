package shu.xai.lyzs.service;

import org.apache.ibatis.annotations.Param;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.entity.modelSelect;

import java.util.List;

public interface dataClusterService {
    List<data_Cluster> getData_cluster(@Param("cluster") String cluster,@Param("clusterAlgorithmValue") String clusterAlgorithmValue);
}

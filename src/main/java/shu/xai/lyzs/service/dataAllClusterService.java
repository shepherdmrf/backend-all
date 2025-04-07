package shu.xai.lyzs.service;

import org.apache.ibatis.annotations.Param;
import shu.xai.lyzs.entity.data_all_Cluster;

import java.util.List;

public interface dataAllClusterService {
    List<data_all_Cluster> getAllData_cluster(@Param("cluster") int cluster,@Param("clusterAlgorithmValue")String clusterAlgorithmValue);
}

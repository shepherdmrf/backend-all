package shu.xai.lyzs.service;

import org.apache.ibatis.annotations.Param;
import shu.xai.lyzs.entity.d_model_cluster;
import shu.xai.lyzs.entity.data_Cluster;

import java.util.List;

public interface ClusterDataService {
    List<d_model_cluster> getCluster_data(@Param("category") String category, @Param("clusterAlgorithmValue") String clusterAlgorithmValue,@Param("selectedCluster") String selectedCluster);
    void updateModel(@Param("category") String category, @Param("clusterAlgorithmValue") String clusterAlgorithmValue,int id, String model_name, String fitness);
}

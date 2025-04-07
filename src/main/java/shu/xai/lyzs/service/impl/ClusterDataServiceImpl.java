package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.d_model_cluster;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.mapper.ClusterDataMapper;
import shu.xai.lyzs.mapper.dataClusterMapper;
import shu.xai.lyzs.service.ClusterDataService;
import shu.xai.lyzs.service.dataClusterService;

import java.util.List;

@Service
public class ClusterDataServiceImpl implements ClusterDataService {
    @Autowired
    ClusterDataMapper clusterDataMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public List<d_model_cluster> getCluster_data(String category, String clusterAlgorithmValue,String selectedCluster){
        return clusterDataMapper.getCluster_data(category,clusterAlgorithmValue,selectedCluster);
    }
    @Override
    public void updateModel(String category, String clusterAlgorithmValue,int id, String model_name, String fitness) {
        clusterDataMapper.updateModel(category,clusterAlgorithmValue,id, model_name, fitness);
    }

}

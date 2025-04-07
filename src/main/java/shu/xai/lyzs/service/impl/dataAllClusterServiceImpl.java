package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.entity.data_all_Cluster;
import shu.xai.lyzs.mapper.dataAllClusterMapper;
import shu.xai.lyzs.service.dataAllClusterService;

import java.util.List;

@Service
public class dataAllClusterServiceImpl implements dataAllClusterService {
    @Autowired
    dataAllClusterMapper dataAllClusterMapper;
    public List<data_all_Cluster> getAllData_cluster(int cluster,String clusterAlgorithmValue){
        return dataAllClusterMapper.getAllData_cluster(cluster,clusterAlgorithmValue);
    }
}

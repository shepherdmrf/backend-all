package shu.xai.lyzs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.mapper.dataClusterMapper;
import shu.xai.lyzs.mapper.modelSelectMapper;
import shu.xai.lyzs.service.dataClusterService;
import shu.xai.lyzs.service.modelSelectService;

import java.util.List;

@Service
public class dataClusterServiceImpl implements dataClusterService {
    @Autowired
    dataClusterMapper dataClusterMapper;
    public List<data_Cluster> getData_cluster(String cluster,String clusterAlgorithmValue){
        return dataClusterMapper.getData_cluster(cluster,clusterAlgorithmValue);
    }
}

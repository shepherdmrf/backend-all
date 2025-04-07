package shu.xai.lyzs.service;

import shu.xai.lyzs.entity.modelSelect;

import java.util.List;

public interface modelSelectService {
    List<modelSelect> getData(String category);
    List<modelSelect> getafterData(String category,String clusterAlgorithmValue);
}

package shu.xai.relativeAnalysis.service;

import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface RelativeService {

    List<KeyKnowledgeFeature> getSVRCluster();

    void generateImagesSvr(int id);

    File getHighDimensionalImage();

    File getLowDimensionalImage();

    File getFValueImage();
}

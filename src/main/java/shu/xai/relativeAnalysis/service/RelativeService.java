package shu.xai.relativeAnalysis.service;

import shu.xai.relativeAnalysis.entity.Feature;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface RelativeService {

    List<Feature> getSVRCluster();

    void generateImagesSvr(int Id);

    File getHighDimensionalImage();

    File getLowDimensionalImage();
}

package shu.xai.dataAnalysis.service;

import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.entity.PageResult;

import java.io.File;
import java.util.Map;

public interface MaterialDataService {
    PageResult<Map<String, Object>> getPaginatedData(int page, int size);

    Map<String, Map<String, Object>> getStatistics();

    File generateViolin();
}

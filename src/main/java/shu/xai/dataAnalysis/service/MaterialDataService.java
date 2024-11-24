package shu.xai.dataAnalysis.service;

import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.entity.PageResult;

import java.util.Map;

public interface MaterialDataService {
    PageResult<MaterialData> getPaginatedData(int page, int size);

    Map<String, Map<String, Object>> getStatistics();

}

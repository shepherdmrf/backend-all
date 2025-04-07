package shu.xai.sdc.service;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ActivationEnergyPredictionService {
    List<Map<String, Object>> predictActivationEnergy(String userName, List<String> filePaths, String modelName) throws Exception;

    List<Map<String, Object>> getPredictionResults(String username, String modelName);

    File getResultsFile(String userName, String modelName) throws Exception;
}
package shu.xai.sdc.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface InterstitialNetworkRepresentationService {
    Map<String, String> convertFiles(List<String> filePaths, String userName, String migrantIon) throws Exception;

    Map<String, String> getCifContentandSpt(String filePath, String username) throws IOException;

    Map<String, Object> previewFile(String userName, String fileName) throws IOException;

    byte[] downloadFiles(String userName, List<String> fileNames) throws IOException;
}

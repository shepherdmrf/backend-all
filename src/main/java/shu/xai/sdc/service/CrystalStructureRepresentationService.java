package shu.xai.sdc.service;

import shu.xai.sdc.entity.Element;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CrystalStructureRepresentationService {

    Map<String, String> convertFiles(List<String> filePaths, String userName, Double radius, Integer max_num_nbr);

    String getCifContent(String filePath, String fileNames, String username) throws IOException;

    List<Element> getAllElements();

    Map<String, Object> previewFile(String userName, String fileName) throws IOException;

    byte[] downloadFiles(String userName, List<String> fileNames) throws IOException;
}

package shu.xai.lyzs.service;

import cn.hutool.json.JSONArray;
import shu.xai.lyzs.entity.DataDescription;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;

import java.util.List;

public interface DataRetrieveService {
    List<DataDescription> getInterData(Integer pageNum, Integer pageSize, String d_abstract, String d_submission_name, String d_submission_unit,
                                       String keywords, String d_size_m);
    List<DataDescription> getCurrentUserData(String currentUser);
    List<PatentData> getPatentData(String maximum, String minimum, String elementName);

    List<PatentData> getDownloadPatentData(String idlist);
    Integer multiDelete(JSONArray arr);
    List<WangLiteData> getWangData();




}

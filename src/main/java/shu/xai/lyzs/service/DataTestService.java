package shu.xai.lyzs.service;

import cn.hutool.json.JSONArray;
import shu.xai.lyzs.entity.DataDescription;
import shu.xai.lyzs.entity.DataTest;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;

import java.util.List;

public interface DataTestService {
    List<DataTest> getTest(Integer pageNum, Integer pageSize);
    List<DataTest> getAll();





}

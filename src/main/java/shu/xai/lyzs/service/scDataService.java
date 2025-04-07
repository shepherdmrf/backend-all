package shu.xai.lyzs.service;

import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.entity.scData;

import java.util.List;

public interface scDataService {
    List<scData> getData(String category);
}

package shu.xai.lyzs.service;

import shu.xai.lyzs.entity.DataCfi;
import shu.xai.lyzs.entity.scData;

import java.util.List;

public interface DataCfiService {
    List<DataCfi> getExpert();
    List<DataCfi> getCfi();
    List<DataCfi> getGini();
    List<DataCfi> getSpearman();
}

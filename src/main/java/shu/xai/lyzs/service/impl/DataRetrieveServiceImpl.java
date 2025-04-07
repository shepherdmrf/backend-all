package shu.xai.lyzs.service.impl;

import cn.hutool.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.DataDescription;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;
import shu.xai.lyzs.mapper.DataRetrieveMapper;
import shu.xai.lyzs.service.DataRetrieveService;

import java.util.List;

@Service
public class DataRetrieveServiceImpl implements DataRetrieveService{

    @Autowired(required = false)
    DataRetrieveMapper dataRetrieveMapper;

    public List<DataDescription> getInterData(Integer pageNum, Integer pageSize, String d_abstract, String d_submission_name, String d_submission_unit,
                                              String keywords, String d_size_m){
        return dataRetrieveMapper.getInterData(pageNum, pageSize, d_abstract, d_submission_name, d_submission_unit, keywords, d_size_m);
    }

    public List<DataDescription> getCurrentUserData(String currentUser){
        return dataRetrieveMapper.getCurrentUserData(currentUser);
    }

    public List<PatentData> getPatentData(String maximum, String minimum, String elementName){
        return dataRetrieveMapper.getPatentData(maximum,minimum,elementName);
    }

    @Override
    public List<PatentData> getDownloadPatentData(String idlist) {

        return dataRetrieveMapper.getDownloadPatentData(idlist);
    }


    public Integer multiDelete(JSONArray arr){
        Integer count = 0;
        for (int i = 0;i<arr.size();i++){
            count += dataRetrieveMapper.multiDelete(arr.getInt(i));

        }
        return count;
    }

    public List<WangLiteData> getWangData(){
        return dataRetrieveMapper.getWangData();
    }
}

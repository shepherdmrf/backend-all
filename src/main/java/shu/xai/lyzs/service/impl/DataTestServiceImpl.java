package shu.xai.lyzs.service.impl;

import cn.hutool.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.lyzs.entity.DataDescription;
import shu.xai.lyzs.entity.DataTest;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;
import shu.xai.lyzs.mapper.DataRetrieveMapper;
import shu.xai.lyzs.mapper.DataTestMapper;
import shu.xai.lyzs.service.DataRetrieveService;
import shu.xai.lyzs.service.DataTestService;

import java.util.List;

@Service
public class DataTestServiceImpl implements DataTestService {

    @Autowired(required = false)
    DataTestMapper dataTestMapper;

    public List<DataTest> getTest(Integer pageNum, Integer pageSize){
        return dataTestMapper.getTest(pageNum, pageSize);
    }
    public List<DataTest> getAll(){
        return dataTestMapper.getAll();
    }


}

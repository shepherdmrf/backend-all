package shu.xai.dataAnalysis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import shu.xai.dataAnalysis.entity.MaterialData;


import java.util.List;

@Mapper
public interface DataMapper {

    @Select("SELECT * FROM data.data2")
    List<MaterialData> getAllData(MaterialData materialData);
}


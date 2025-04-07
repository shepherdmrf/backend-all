package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.DataCad;
import shu.xai.lyzs.entity.DataCfi;

import java.util.List;

@Mapper
@Repository
public interface DataCadMapper extends BaseMapper<DataCad> {

    @Select("select cluster2,cluster3,cluster4,cluster5,cluster6,cluster7,cluster8,cluster9,cluster10,cluster11 from lyzs.data_cad where model_name = #{clusterAlgorithmValue} ")
    List<DataCad> getCad(@Param("clusterAlgorithmValue") String clusterAlgorithmValue);
}

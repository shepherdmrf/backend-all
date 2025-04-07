package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.modelSelect;

import java.util.List;

@Mapper
@Repository
public interface modelSelectMapper extends BaseMapper<modelSelect> {
//    @Select("select id,model_name,fitness from sys_fitness_model where category='8'")
    @Select("select id,model_name,fitness from lyzs.sys_fitness_model where category=#{category}")
    List<modelSelect> getData(@Param("category") String category);
    @Select("select ath_id,model_name,fitness from lyzs.sys_fitness_model where category=#{category} AND ath_name=#{clusterAlgorithmValue}")
    List<modelSelect> getafterData(@Param("category") String category,@Param("clusterAlgorithmValue") String clusterAlgorithmValue);

}

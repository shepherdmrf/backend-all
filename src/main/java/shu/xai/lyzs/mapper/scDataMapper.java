package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.entity.scData;

import java.util.List;

@Mapper
@Repository
public interface scDataMapper extends BaseMapper<scData> {
//    @Select("select id,model_name,fitness from sys_fitness_model where category='8'")
    @Select("select id,sc,dbi,chi from lyzs.data_sc where category=#{category}")
    List<scData> getData(@Param("category") String category);

}

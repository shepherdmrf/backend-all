package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.DataCad;
import shu.xai.lyzs.entity.DataTest;

import java.util.List;

@Mapper
@Repository
public interface DataTestMapper extends BaseMapper<DataTest> {

    @Select("SELECT * FROM lyzs.data_test limit #{offset}, #{pageSize} ")
    List<DataTest> getTest(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    @Select("SELECT * FROM lyzs.data_test")
    List<DataTest> getAll();
}

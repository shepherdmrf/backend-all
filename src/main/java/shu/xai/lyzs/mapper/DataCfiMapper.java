package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.DataCfi;
import shu.xai.lyzs.entity.scData;

import java.util.List;

@Mapper
@Repository
public interface DataCfiMapper extends BaseMapper<DataCfi> {
//    @Select("select id, Ni_wt,Ru_wt,Re_wt,Co_wt,Al_wt,Ti_wt,W_wt,Mo_wt,Cr_wt,Ta_wt,C_wt,B_wt,Y_wt,Nb_wt,Hf_wt,solution_treatment_time,first_step_aging_treatment_time,second_step_aging_treatment_time,solution_treatment_temperature,first_step_aging_treatment_temperature,second_step_aging_treatment_temperature,temperature,applied_Stress from data_cfi where id=1")
    @Select("select * from lyzs.data_cfi where id=1")
    List<DataCfi> getExpert();
    @Select("select * from lyzs.data_cfi where id=4")
    List<DataCfi> getCfi();
    @Select("select * from lyzs.data_cfi where id=3")
    List<DataCfi> getGini();
    @Select("select * from lyzs.data_cfi where id=2")
    List<DataCfi> getSpearman();
}

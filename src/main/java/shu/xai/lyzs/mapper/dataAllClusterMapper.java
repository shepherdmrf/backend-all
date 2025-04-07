package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.entity.data_all_Cluster;

import java.util.List;

@Mapper
@Repository
public interface dataAllClusterMapper extends BaseMapper<data_all_Cluster> {
//    @Select("select id,model_name,fitness from sys_fitness_model where category='8'")
    @Select("select id,Ni_wt,Ru_wt,Re_wt,Co_wt,Al_wt,Ti_wt,W_wt,Mo_wt,Cr_wt,Ta_wt,C_wt,B_wt,Y_wt,Nb_wt,Hf_wt,solution_treatment_time,fst_step_aging_treatment_time,snd_step_aging_treatment_time,solution_treatment_temperature,fst_step_aging_treatment_temperature,snd_step_aging_treatment_temperature,temperature,applied_Stress,creep_life,category from lyzs.data_all_cluster where cluster=#{cluster} AND model_name = #{clusterAlgorithmValue}")
    List<data_all_Cluster> getAllData_cluster(@Param("cluster") int cluster,@Param("clusterAlgorithmValue") String clusterAlgorithmValue);

}

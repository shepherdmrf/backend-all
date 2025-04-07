package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.data_Cluster;
import shu.xai.lyzs.entity.modelSelect;

import java.util.List;

@Mapper
@Repository
public interface dataClusterMapper extends BaseMapper<data_Cluster> {
//    @Select("select id,model_name,fitness from sys_fitness_model where category='8'")
    @Select("select abscissa,ordinate,id,pc3,category from lyzs.data_cluster where cluster=#{cluster} AND model_name = #{clusterAlgorithmValue}")
    List<data_Cluster> getData_cluster(@Param("cluster") String cluster,@Param("clusterAlgorithmValue") String clusterAlgorithmValue);

}

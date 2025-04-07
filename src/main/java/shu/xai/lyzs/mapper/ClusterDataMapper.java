package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import shu.xai.lyzs.entity.d_model_cluster;
import shu.xai.lyzs.entity.data_Cluster;

import java.util.List;

@Mapper
@Repository
public interface ClusterDataMapper extends BaseMapper<d_model_cluster> {
//    @Select("select id,model_name,fitness from sys_fitness_model where category='8'")
    @Select("select id,model_name,fitness from lyzs.d_model_cluster where category=#{category} AND ath_name = #{clusterAlgorithmValue}AND id = #{selectedCluster}")
    List<d_model_cluster> getCluster_data(@Param("category") String category, @Param("clusterAlgorithmValue") String clusterAlgorithmValue,@Param("selectedCluster")String seletedCluster);

    @Update("UPDATE lyzs.sys_fitness_model SET model_name = #{model_name}, fitness = #{fitness} WHERE category=#{category} AND ath_name = #{clusterAlgorithmValue} AND ath_id = #{id}")
    int updateModel(@Param("category") String category, @Param("clusterAlgorithmValue") String clusterAlgorithmValue,@Param("id") int id, @Param("model_name") String model_name, @Param("fitness") String fitness);

}

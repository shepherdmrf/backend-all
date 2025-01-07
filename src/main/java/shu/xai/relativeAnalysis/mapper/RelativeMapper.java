package shu.xai.relativeAnalysis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;

import java.util.List;

@Mapper
public interface RelativeMapper {

    @Select("SELECT id, Features FROM data_table.bestsvr")
    List<KeyKnowledgeFeature> getSVRCluster();

    @Select("SELECT Features FROM data_table.bestsvr WHERE id = #{id}")
    String findSVRById(int id);
}

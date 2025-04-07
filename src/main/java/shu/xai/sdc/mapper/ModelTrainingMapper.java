package shu.xai.sdc.mapper;

import org.apache.ibatis.annotations.*;
import shu.xai.sdc.entity.ModelData;

import java.util.List;

@Mapper
public interface ModelTrainingMapper {

    // 查询所有模型
    @Select("SELECT id, name, model_path AS modelPath, introduction_file AS introductionFile, architecture_file AS architectureFile FROM xai.models")
    List<ModelData> getAllModels();

    // 根据 ID 查询模型
    @Select("SELECT id, name, model_path AS modelPath, introduction_file AS introductionFile, architecture_file AS architectureFile FROM xai.models WHERE id = #{id}")
    ModelData getModelById(@Param("id") Integer id);

    // 插入新模型
    @Insert("INSERT INTO xai.models (name, model_path, introduction_file, architecture_file) " +
            "VALUES (#{name}, #{modelPath}, #{introductionFile}, #{architectureFile})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ModelData modelData);

    // 根据 ID 删除模型
    @Delete("DELETE FROM xai.models WHERE id = #{id}")
    void deleteById(@Param("id") Integer id);

}
package shu.xai.characteristicClusterConstruction.mapper;

import org.apache.ibatis.annotations.*;
import shu.xai.characteristicClusterConstruction.entity.ExcelRowData;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;

import java.util.List;
import java.util.Map;

@Mapper
public interface FeatureMapper {

    @Select("SELECT feature FROM feature_table.mlr_knowlegekernel "+
            "WHERE `CV RMSE` = (SELECT MAX(`CV RMSE`) FROM feature_table.mlr_knowlegekernel) " +
            "ORDER BY `Test RMSE` ASC LIMIT 1")
    List<KeyKnowledgeFeature> findKnowledgeMLR();

    @Select("SELECT feature FROM feature_table.knn_knowlegekernel "+
            "WHERE `CV RMSE` = (SELECT MAX(`CV RMSE`) FROM feature_table.knn_knowlegekernel) " +
            "ORDER BY `Test RMSE` ASC LIMIT 1")
    List<KeyKnowledgeFeature> findKnowledgeKNN();

    @Select("SELECT feature FROM feature_table.svr_knowlegekernel "+
            "WHERE `CV RMSE` = (SELECT MAX(`CV RMSE`) FROM feature_table.svr_knowlegekernel) " +
            "ORDER BY `Test RMSE` ASC LIMIT 1")
    List<KeyKnowledgeFeature> findKnowledgeSVR();

    @Select("SELECT feature FROM feature_table.gpr_knowlegekernel "+
            "WHERE `CV RMSE` = (SELECT MAX(`CV RMSE`) FROM feature_table.gpr_knowlegekernel) " +
            "ORDER BY `Test RMSE` ASC LIMIT 1")
    List<KeyKnowledgeFeature> findKnowledgeGPR();

    @Select("SELECT positive_kernel FROM feature_table.mlr_positive_kernel")
    List<Feature> findDataMLR();

    @Select("SELECT positive_kernel FROM feature_table.knn_positive_kernel")
    List<Feature> findDataKNN();

    @Select("SELECT positive_kernel FROM feature_table.svr_positive_kernel")
    List<Feature> findDataSVR();

    @Select("SELECT positive_kernel FROM feature_table.gpr_positive_kernel")
    List<Feature> findDataGPR();

    @Select("SELECT features FROM data.mlr_positivekernel WHERE id = #{id}")
    String findMLRById(@Param("id") int id);

    @Select("SELECT features FROM data.knn_positivekernel WHERE id = #{id}")
    String findKNNById(@Param("id") int id);

    @Select("SELECT features FROM data.svr_positivekernel WHERE id = #{id}")
    String findSVRById(@Param("id") int id);

    @Select("SELECT features FROM data.gpr_positivekernel WHERE id = #{id}")
    String findGPRById(@Param("id") int id);

    @Select("SELECT id, feature\n" +
            "FROM data.key_knowledgekernel\n" +
            "WHERE model = #{model}")
    List<KeyKnowledgeFeature> findByModel(String model);

    @Insert({
            "<script>",
            "INSERT INTO data_table.${tableName} (Features, Length, Score, CV_RMSE, RMSE, R2, Time) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.features}, #{item.length}, #{item.score}, #{item.CV_RMSE}, #{item.rmse}, #{item.r2}, #{item.time})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("list") List<ExcelRowData> rows, @Param("tableName") String tableName);

    @Select("SELECT * FROM data_table.${tableName}")
    List<ExcelRowData> queryDataByTable(@Param("tableName") String tableName);


    @Delete("DELETE FROM data_table.${tableName}")
    void clearTable(@Param("tableName") String tableName);


    @Select("SELECT * FROM data_table.${tableName} " +
            "WHERE CV_RMSE = (SELECT MAX(CV_RMSE) FROM data_table.${tableName}) " +
            "ORDER BY RMSE ASC LIMIT 1")
    ExcelRowData queryMaxCvRmseRow(@Param("tableName") String tableName);


    @Insert("INSERT INTO data_table.${tableName1} (id, features, length, score, CV_RMSE, rmse, r2, time) " +
            "VALUES (#{maxCvRmseRow.id}, #{maxCvRmseRow.features}, #{maxCvRmseRow.length}, #{maxCvRmseRow.score}, " +
            "#{maxCvRmseRow.CV_RMSE}, #{maxCvRmseRow.rmse}, #{maxCvRmseRow.r2}, #{maxCvRmseRow.time})")
    void insertIntoMaxCvRmseTable(@Param("maxCvRmseRow") ExcelRowData maxCvRmseRow, @Param("tableName1") String tableName1);

    @Insert({
            "<script>",
            "INSERT INTO data_table.${tableName} (id, features, length, score, CV_RMSE, rmse, r2, time)",
            "VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.id}, #{item.features}, #{item.length}, #{item.score}, #{item.CV_RMSE}, #{item.rmse}, #{item.r2}, #{item.time})",
            "</foreach>",
            "</script>"
    })
    void insertBatchWithId(@Param("list") List<ExcelRowData> list, @Param("tableName") String tableName);

    @Select("SELECT id FROM data_table.${tableName} WHERE features = #{feature}")
    int findIDbyTableNameAndFeature(@Param("tableName") String tableName, @Param("feature") String feature3);

    @Select("SELECT Features FROM data_table.${tablename} WHERE id = #{id}")
    String findBestById(String tablename, int id);

    @Select("SELECT * FROM data_table.${tablename} WHERE Features = #{features}")
    ExcelRowData queryBestRowByFeatures(String tablename, String features);
}


package shu.xai.dataAnalysis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import shu.xai.dataAnalysis.entity.MaterialData;

import java.util.List;
import java.util.Map;

@Mapper
public interface MaterialDataMapper {
    void insertMaterialData(@Param("dataList") List<MaterialData> dataList);

    String FIELDS = "Occu_6b, Occu_18e, Occu_36f, C_Na,"+
            "Occu_M1, Occu_M2, EN_M1, EN_M2,"+
            "avg_EN_M, Radius_M1, Radius_M2, avg_Radius_M,"+
            "Valence_M1, Valence_M2, avg_Valence_M,"+
            "Occu_X1, Occu_X2, EN_X1, EN_X2,"+
            "avg_EN_X, Radius_X1, Radius_X2, avg_Radius_X,"+
            "Valence_X1, Valence_X2, avg_Valence_X,"+
            "a, c, V, V_MO6, V_XO4,"+
            "V_Na1_O6, V_Na2_O8, V_Na3_O5,"+
            "BT2, BT1, Min_BT, RT,"+
            "Entropy_6b, Entropy_18e, Entropy_36f,"+
            "Entropy_Na, Entropy_M, Entropy_X,"+
            "T, BVSE_energy_barrier";


    // 查询当前页数据
    @Select("SELECT " + FIELDS + " FROM data.data2 LIMIT #{offset}, #{size}")
    List<MaterialData> getPaginatedData(@Param("offset") int offset, @Param("size") int size);

    // 查询总记录数
    @Select("SELECT COUNT(*) FROM data.data2")
    int getTotalCount();

    @Select("SELECT MAX(${attribute}) FROM data.data2")
    Double getMaxValue(@Param("attribute") String attribute);

    @Select("SELECT MIN(${attribute}) FROM data.data2")
    Double getMinValue(@Param("attribute") String attribute);

    @Select("SELECT AVG(${attribute}) FROM data.data2")
    Double getAverageValue(@Param("attribute") String attribute);

    @Select("SELECT COUNT(${attribute}) FROM data.data2 WHERE ${attribute} IS NOT NULL")
    int getNonNullCount(@Param("attribute") String attribute);

    @Select("SELECT ${attribute} FROM data.data2 WHERE ${attribute} IS NOT NULL ORDER BY ${attribute} LIMIT 1 OFFSET #{offset}")
    Double getMedianValueWithOffset(@Param("attribute") String attribute, @Param("offset") int offset);

    @Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'data2' AND TABLE_SCHEMA = 'data'")
    List<String> getColumnNames();
}




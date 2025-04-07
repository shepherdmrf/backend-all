package shu.xai.lyzs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import shu.xai.lyzs.entity.DataDescription;
import shu.xai.lyzs.entity.PatentData;
import shu.xai.lyzs.entity.WangLiteData;

import java.util.List;

@Mapper
public interface DataRetrieveMapper extends BaseMapper<DataDescription> {

    @Select("SELECT * FROM lyzs.data_description WHERE d_abstract LIKE concat('%', #{d_abstract}, '%') AND " +
            "d_submission_name LIKE concat('%', #{d_submission_name}, '%') AND " +
            "d_submission_unit LIKE concat('%', #{d_submission_unit}, '%') AND " +
            "keywords LIKE concat('%', #{keywords}, '%') AND " +
            "d_size_m >= #{d_size_m} ")
//            "limit #{pageNum}, #{pageSize}")
    List<DataDescription> getInterData(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("d_abstract") String d_abstract,
                                       @Param("d_submission_name") String d_submission_name, @Param("d_submission_unit") String d_submission_unit,
                                       @Param("keywords") String keywords, @Param("d_size_m") String d_size_m);
    //todo String d_size_m小问题，int和String其实也能比较，不太想管


    @Select("select * from lyzs.data_description where op_user = #{currentUser}")
    List<DataDescription> getCurrentUserData(@Param("currentUser") String currentUser);

    @Select("select * from lyzs.patent_data where ${elementName} >= #{minimum} " +
            "and ${elementName} <= #{maximum}")
    List<PatentData> getPatentData(@Param("maximum") String maximum, @Param("minimum") String minimum, @Param("elementName") String elementName);

    @Select("select * from lyzs.patent_data where Alloy_Index in (${idlist})")
    List<PatentData> getDownloadPatentData(@Param("idlist") String idlist);

    @Delete("delete from lyzs.patent_data where Alloy_Index = #{a}")
    Integer multiDelete(@Param("a") Integer a);

    //    @Select("select * from data_description where d_submission_name = '321'")
    @Select("select * from lyzs.wanglitedata")
    List<WangLiteData> getWangData();
}

package shu.xai.sdc.mapper;

import shu.xai.sdc.entity.Element;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ElementMapper {

    @Select("SELECT atomic_number AS atomicNumber, element_name AS elementName FROM xai.element_table")
    List<Element> getAllElements();
}
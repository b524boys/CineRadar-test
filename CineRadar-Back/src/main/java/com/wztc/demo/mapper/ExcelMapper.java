package com.wztc.demo.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExcelMapper {
    /**
     * 动态SQL查询
     * @param sqlStr
     * @return
     */
    @MapKey("")
    List<Map> dynamicSqlOperDbData(@Param("sqlStr") String sqlStr);
}

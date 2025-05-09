package com.wztc.demo.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecommendMapper {
    /**
     * 根据权重查询推荐列表
     */
    @MapKey(value = "goods_id")
    List<Map<Object,Object>> recommendListByWeight(
            @Param("collectWeight") Integer collectWeight,
            @Param("historyWeight") Integer historyWeight,
            @Param("commentWeight") Integer commentWeight);
}

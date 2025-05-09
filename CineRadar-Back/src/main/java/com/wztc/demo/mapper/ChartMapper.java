package com.wztc.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface ChartMapper {
    /**
     * 用户总数
     * @return
     */
    Integer getUserCount();

    /**
     * 电影总数
     * @return
     */
    Integer getGoodsCount();

    /**
     * 浏览总数
     * @return
     */
    Integer getHitCount();

    /**
     * 评论总数
     * @return
     */
    Integer getCommentsCount();

    /**
     * 统计用户注册情况
     * @return
     */
    List<Map> userRegister(Map<String,String> dataMap);

    /**
     * 统计类型占比
     * @return
     */
    public List<Map> cateCount();

    /**
     * 统计演员电影演出--柱状图
     * @return
     */
    public List<Map> castCount();

    /**
     * 统计各年份电影上映数量--折线图
     * @return
     */
    public List<Map> yearCount();

}
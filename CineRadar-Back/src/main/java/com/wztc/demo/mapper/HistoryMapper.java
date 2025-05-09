package com.wztc.demo.mapper;

import com.wztc.demo.entity.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 足迹 Mapper层
 */
@Mapper
public interface HistoryMapper {
    /**
     * 根据主键查询
     */
    History selectById(Integer id);

    /**
     * 条件查询
     */
    List<History> select(History history);

    /**
     * 查询每个用户浏览商品次数
     */
    List<History> selectViewCountByHit();

    /**
     * 新增数据
     */
    int insert(History user);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
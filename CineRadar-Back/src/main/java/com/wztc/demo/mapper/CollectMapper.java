package com.wztc.demo.mapper;

import com.wztc.demo.entity.Collect;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 收藏 Mapper层
 */
@Mapper
public interface CollectMapper {
    /**
     * 根据主键查询
     */
    Collect selectById(Integer id);

    /**
     * 条件查询
     */
    List<Collect> select(Collect collect);

    /**
     * 新增数据
     */
    int insert(Collect user);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
package com.wztc.demo.mapper;

import com.wztc.demo.entity.Carousel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 轮播图 Mapper层
 */
@Mapper
public interface CarouselMapper {
    /**
     * 根据主键查询
     */
    Carousel selectById(Integer id);

    /**
     * 条件查询
     */
    List<Carousel> select(Carousel carousel);

    /**
     * 新增数据
     */
    int insert(Carousel carousel);

    /**
     * 更新数据
     */
    int update(Carousel category);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}

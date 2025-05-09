package com.wztc.demo.mapper;

import com.wztc.demo.entity.News;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 公告 Mapper层
 */
@Mapper
public interface NewsMapper {
    /**
     * 根据主键查询
     */
    News selectById(Integer id);


    /**
     * 存储过程   根据主键查询
     */
    News selectByIdProcedure(Integer id);

    /**
     * 条件查询
     */
    List<News> select(News news);

    /**
     * 视图  条件查询
     */
    List<News> selectByView(News news);


    /**
     * 新增数据
     */
    int insert(News news);

    /**
     * 更新数据
     */
    int update(News news);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);


    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
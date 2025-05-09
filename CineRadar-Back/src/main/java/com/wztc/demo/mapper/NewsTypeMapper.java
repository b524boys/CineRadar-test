package com.wztc.demo.mapper;

import com.wztc.demo.entity.NewsType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 公告分类 Mapper层
 */
@Mapper
public interface NewsTypeMapper {
    /**
     * 根据主键查询
     */
    NewsType selectById(Integer id);

    /**
     * 条件查询
     */
    List<NewsType> select(NewsType newsType);


    /**
     * 新增数据
     */
    int insert(NewsType newsType);

    /**
     * 更新数据
     */
    int update(NewsType newsType);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);


    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}


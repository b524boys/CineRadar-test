package com.wztc.demo.mapper;

import com.wztc.demo.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类 Mapper层
 */
@Mapper
public interface CategoryMapper {
    /**
     * 根据主键查询
     */
    Category selectById(Integer id);

    /**
     * 条件查询   名称是模糊匹配
     */
    List<Category> select(Category category);

    /**
     * 新增数据
     */
    int insert(Category category);

    /**
     * 更新数据
     */
    int update(Category category);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}

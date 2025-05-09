package com.wztc.demo.mapper;

import com.wztc.demo.entity.Comments;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 评论 Mapper层
 */
@Mapper
public interface CommentsMapper {
    /**
     * 根据主键查询
     */
    Comments selectById(Integer id);

    /**
     * 条件查询
     */
    List<Comments> select(Comments comments);

    /**
     * 查询所有用户评分
     */
    List<Comments> selectCommentsRate();


    /**
     * 新增数据
     */
    int insert(Comments comments);

    /**
     * 更新数据
     */
    int update(Comments comments);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
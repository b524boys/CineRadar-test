package com.wztc.demo.mapper;

import com.wztc.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户 Mapper层
 */
@Mapper
public interface UserMapper {
    /**
     * 根据主键查询
     */
    User selectById(Integer id);

    /**
     * 条件查询
     */
    List<User> select(User user);

    /**
     * 新增数据
     */
    int insert(User user);

    /**
     * 更新数据
     */
    int update(User user);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
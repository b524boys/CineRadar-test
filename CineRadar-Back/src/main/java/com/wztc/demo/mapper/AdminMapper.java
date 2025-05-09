package com.wztc.demo.mapper;

import com.wztc.demo.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 管理员 Mapper层
 */
@Mapper
public interface AdminMapper {
    /**
     * 根据主键查询
     */
    Admin selectById(Integer id);

    /**
     * 条件查询
     */
    List<Admin> select(Admin admin);


    /**
     * 新增数据
     */
    int insert(Admin admin);

    /**
     * 更新数据
     */
    int update(Admin admin);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);


    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}

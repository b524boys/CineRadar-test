package com.wztc.demo.mapper;

import com.wztc.demo.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统日志 Mapper层
 */
@Mapper
public interface SysLogMapper {
    /**
     * 根据主键查询
     */
    SysLog selectById(Integer id);

    /**
     * 条件查询
     */
    List<SysLog> select(SysLog sysLog);


    /**
     * 新增数据
     */
    int insert(SysLog sysLog);

    /**
     * 更新数据
     */
    int update(SysLog sysLog);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);


    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}


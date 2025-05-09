package com.wztc.demo.mapper;

import com.wztc.demo.entity.Attach;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 附件 Mapper层
 */
@Mapper
public interface AttachMapper {
    /**
     * 根据主键查询
     */
    Attach selectById(Integer id);

    /**
     * 条件查询
     */
    List<Attach> select(Attach attach);

    /**
     * 新增数据
     */
    int insert(Attach attach);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
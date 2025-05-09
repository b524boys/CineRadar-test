package com.wztc.demo.mapper;

import com.wztc.demo.entity.MessageBoard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 留言板 Mapper层
 */
@Mapper
public interface MessageBoardMapper {
    /**
     * 根据主键查询
     */
    MessageBoard selectById(Integer id);

    /**
     * 条件查询
     */
    List<MessageBoard> select(MessageBoard messageBoard);


    /**
     * 新增数据
     */
    int insert(MessageBoard messageBoard);

    /**
     * 更新数据
     */
    int update(MessageBoard messageBoard);

    /**
     * 根据id删除数据
     */
    int deleteById(Integer id);

    /**
     * 根据ids批量删除
     */
    int deleteBatch(List<Integer> ids);
}
package com.wztc.demo.service;

import com.wztc.demo.entity.History;
import com.wztc.demo.mapper.HistoryMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 足迹 Service层
 */
@Service
public class HistoryService {

    @Autowired
    private HistoryMapper historyMapper;


    /**
     * 根据主键查询
     */
    public History selectById(Integer id){
        return historyMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<History> selectByPage(History history, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return historyMapper.select(history); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<History> select(History history) {
        return historyMapper.select(history);
    }

    /**
     * 新增数据
     */
    public int add(History history) {
        history.setCreateTime(DateUtils.date2ShortString(new Date()));
        return historyMapper.insert(history);
    }


    /**
     * 根据id删除数据
     */
    public int deleteById(Integer id) {
        return historyMapper.deleteById(id);
    }


    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        historyMapper.deleteBatch(ids);
    }

}

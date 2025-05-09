package com.wztc.demo.service;

import com.wztc.demo.entity.NewsType;
import com.wztc.demo.mapper.NewsTypeMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告类型 Service层
 */
@Service
public class NewsTypeService {

    @Autowired
    private NewsTypeMapper newsTypeMapper;

    /**
     * 根据主键查询
     */
    public NewsType selectById(Integer id){
        return newsTypeMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<NewsType> selectByPage(NewsType newsType, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return newsTypeMapper.select(newsType); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<NewsType> select(NewsType newsType) {
        return newsTypeMapper.select(newsType);
    }

    /**
     * 新增数据
     */
    public void add(NewsType newsType) {
        newsType.setCreateTime(DateUtils.date2ShortString(new Date()));
        newsTypeMapper.insert(newsType);
    }

    /**
     * 修改数据
     */
    public void update(NewsType newsType) {
        newsTypeMapper.update(newsType);
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        newsTypeMapper.deleteById(id);
    }

    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        newsTypeMapper.deleteBatch(ids);
    }
}
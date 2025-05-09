package com.wztc.demo.service;

import com.wztc.demo.entity.News;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.NewsMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告 Service层
 */
@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 根据主键查询
     */
    public News selectById(Integer id){
        return newsMapper.selectById(id);
    }

    /**
     * 存储过程   根据主键查询
     */
    public News selectByIdProcedure(Integer id){
        return newsMapper.selectByIdProcedure(id);
    }

    /**
     * 分页查询数据
     */
    public List<News> selectByPage(News news, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return newsMapper.select(news); // 执行分页查询
    }

    /**
     * 分页查询数据   视图
     */
    public List<News> selectByPageByView(News news, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return newsMapper.selectByView(news); // 执行分页查询
    }

    /**
     * 查询数据
     */
    public List<News> select(News news) {
        return newsMapper.select(news); // 执行查询
    }

    /**
     * 新增数据
     */
    public void add(News news) {
        news.setHits(0);
        news.setCreateTime(DateUtils.date2ShortString(new Date()));
        try{
            newsMapper.insert(news);
        }catch (DuplicateKeyException e){
            throw new ServiceException("新增时，标题重复");
        }

    }

    /**
     * 修改数据
     */
    public void update(News news) {
        try{
            newsMapper.update(news);
        }catch (DuplicateKeyException e){
            throw new ServiceException("修改时，标题重复");
        }

    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        newsMapper.deleteById(id);
    }


    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        newsMapper.deleteBatch(ids);
    }
}
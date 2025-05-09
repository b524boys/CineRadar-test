package com.wztc.demo.service;

import com.wztc.demo.entity.Collect;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.CollectMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏 Service层
 */
@Service
public class CollectService {

    @Autowired
    private CollectMapper collectMapper;


    /**
     * 根据主键查询
     */
    public Collect selectById(Integer id){
        return collectMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Collect> selectByPage(Collect collect, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return collectMapper.select(collect); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<Collect> select(Collect collect) {
        return collectMapper.select(collect);
    }

    /**
     * 新增数据
     */
    public int add(Collect collect) {
        try{
            return collectMapper.insert(collect);
        }catch(DuplicateKeyException e){
            throw new ServiceException("这个用户已经收藏过此电影");
        }

    }

    /**
     * 根据id删除数据
     */
    public int deleteById(Integer id) {
        return collectMapper.deleteById(id);
    }

    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        collectMapper.deleteBatch(ids);
    }
}

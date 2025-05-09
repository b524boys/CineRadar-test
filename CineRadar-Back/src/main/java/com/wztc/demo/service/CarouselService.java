package com.wztc.demo.service;

import com.wztc.demo.entity.Carousel;
import com.wztc.demo.mapper.CarouselMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图 Service层
 */
@Service
public class CarouselService {
    @Autowired
    private CarouselMapper carouselMapper;
    /**
     * 根据主键查询
     */
    public Carousel selectById(Integer id){
        return carouselMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Carousel> selectByPage(Carousel category, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return carouselMapper.select(category); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<Carousel> select(Carousel category) {
        return carouselMapper.select(category);
    }

    /**
     * 新增数据
     */
    public void add(Carousel carousel) {
        carouselMapper.insert(carousel);
    }

    /**
     * 修改数据
     */
    public void update(Carousel category) {
        carouselMapper.update(category);
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        carouselMapper.deleteById(id);
    }


    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        carouselMapper.deleteBatch(ids);
    }
}

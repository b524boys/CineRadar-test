package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wztc.demo.entity.Category;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.CategoryMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图 Service层
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 根据主键查询
     */
    public Category selectById(Integer id){
        return categoryMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Category> selectByPage(Category category, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return categoryMapper.select(category); // 执行分页查询
    }

    /**
     * 查询所有
     */
    public List<Category> select(Category category) {
        return categoryMapper.select(category);
    }

    /**
     * 新增数据
     */
    public void add(Category category) {
        Category categoryQuery = new Category();
        categoryQuery.setCateName(category.getCateName());
        List<Category> lstCategory = categoryMapper.select(categoryQuery);
        categoryQuery = CollectionUtil.isEmpty(lstCategory) ? null : lstCategory.get(0);

        if(ObjectUtil.isNotEmpty(categoryQuery)){
            throw new ServiceException("分类名已经存在");
        }
        categoryMapper.insert(category);
    }

    /**
     * 修改数据
     */
    public void update(Category category) {
        Category categoryQuery = new Category();
        categoryQuery.setCateName(category.getCateName());
        List<Category> lstCategory = categoryMapper.select(categoryQuery);
        categoryQuery = CollectionUtil.isEmpty(lstCategory) ? null : lstCategory.get(0);

        if(ObjectUtil.isNotEmpty(categoryQuery) && category.getId().intValue() != categoryQuery.getId().intValue()){
            throw new ServiceException("分类名已经存在");
        }
        categoryMapper.update(category);
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        categoryMapper.deleteById(id);
    }

    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        categoryMapper.deleteBatch(ids);
    }
}

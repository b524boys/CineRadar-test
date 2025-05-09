package com.wztc.demo.controller;


import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.Category;
import com.wztc.demo.service.CategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类 Controller层  -> 对应表: category
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(Category category,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Category> lstCategory = categoryService.selectByPage(category, pageNum, pageSize);
        PageInfo<Category> pageInfo = new PageInfo<>(lstCategory);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Category>(total, lstCategory));
    }

    /**
     * 查询所有数据
     */
    @GetMapping("/select")
    @NoTokenAccess
    public Response select(Category category) {
        List<Category> lstCategory = categoryService.select(category);
        return Response.success(lstCategory);
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody Category category){
        categoryService.add(category);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody Category category){
        categoryService.update(category);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        categoryService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        categoryService.deleteBatch(ids);
        return Response.success();
    }
}

package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.common.SysOperation;
import com.wztc.demo.entity.NewsType;
import com.wztc.demo.service.NewsTypeService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告类型Controller层  -> 对应表: news_type
 */
@RestController
@RequestMapping("/newsType")
public class NewsTypeController {

    @Autowired
    private NewsTypeService newsTypeService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(NewsType newsType,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<NewsType> lstNewsType = newsTypeService.selectByPage(newsType, pageNum, pageSize);
        PageInfo<NewsType> pageInfo = new PageInfo<>(lstNewsType);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<NewsType>(total, lstNewsType));
    }

    /**
     * 查询所有
     */
    @GetMapping("/select")
    @NoTokenAccess
    public Response select(NewsType newsType) {
        List<NewsType> lstNewsType = newsTypeService.select(newsType);
        return Response.success(lstNewsType);
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    @SysOperation("新增公告类型")
    public Response add(@RequestBody NewsType newsType){
        newsTypeService.add(newsType);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody NewsType newsType){
        newsTypeService.update(newsType);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        newsTypeService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        newsTypeService.deleteBatch(ids);
        return Response.success();
    }
}

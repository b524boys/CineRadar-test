package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.News;
import com.wztc.demo.service.NewsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告Controller层  -> 对应表: news
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 分页查询
     */
    @NoTokenAccess
    @GetMapping("/selectByPage")
    public Response selectByPage(News news,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<News> lstNews = newsService.selectByPage(news, pageNum, pageSize);
        PageInfo<News> pageInfo = new PageInfo<>(lstNews);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<News>(total, lstNews));
    }

    /**
     * 查询
     */
    @NoTokenAccess
    @GetMapping("/select")
    public Response select(News news) {
        List<News> lstNews = newsService.select(news);
        return Response.success(lstNews);
    }

    /**
     * 查询详情
     */
    @NoTokenAccess
    @GetMapping("/detail/{id}")
    public Response selectById(@PathVariable Integer id){
        News news = newsService.selectById(id);
        return Response.success(news);
    }



    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody News news){
        newsService.add(news);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody News news){
        newsService.update(news);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        newsService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        newsService.deleteBatch(ids);
        return Response.success();
    }
}

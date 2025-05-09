package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.Carousel;
import com.wztc.demo.service.CarouselService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图 Controller层  -> 对应表: carousel
 */
@RestController
@RequestMapping("/carousel")
public class CarouselController {
    @Autowired
    private CarouselService carouselService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(Carousel carousel,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Carousel> lstCarousel = carouselService.selectByPage(carousel, pageNum, pageSize);
        PageInfo<Carousel> pageInfo = new PageInfo<>(lstCarousel);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Carousel>(total, lstCarousel));
    }

    /**
     * 查询所有
     */
    @GetMapping("/select")
    @NoTokenAccess
    public Response select(Carousel carousel) {
        List<Carousel> lstCarousel = carouselService.select(carousel);
        return Response.success(lstCarousel);
    }

    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody Carousel carousel){
        carouselService.add(carousel);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    public Response update(@RequestBody Carousel carousel){
        carouselService.update(carousel);
        return Response.success();
    }

    /**
     * 根据id删除数据
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        carouselService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        carouselService.deleteBatch(ids);
        return Response.success();
    }
}

package com.wztc.demo.controller;


import com.wztc.demo.common.*;
import com.wztc.demo.common.*;
import com.wztc.demo.entity.Goods;
import com.wztc.demo.service.GoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 电影 Controller层  -> 对应表: goods
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询(Elasticsearch)
     */
    @GetMapping("/selectByPageES")
    @NoTokenAccess
    public Response selectByPageES(Goods goods,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ElasticSearchResult<Goods> result = goodsService.selectByPageES(goods, pageNum, pageSize);
            System.out.println("ElasticSearch查询成功");
            return Response.success(new PageCommon<Goods>(result.getTotal(), result.getItems()));
        } catch (IOException e) {
            System.out.println("Error!!! ElasticSearch查询失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 分页查询(MySQL)
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(Goods goods,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Goods> lstGoods = goodsService.selectByPage(goods, pageNum, pageSize);
//        lstGoods.forEach(item->{
//            String cateName = item.getCateName();
//            if (!StringUtils.isEmpty(cateName)){
//                List<String> lstCateName = Arrays.asList(cateName.split(","));
//                item.setCateNameList(lstCateName);
//            }
//        });
        PageInfo<Goods> pageInfo = new PageInfo<>(lstGoods);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Goods>(total, lstGoods));
    }

    /**
     * 查询所有
     */
    @GetMapping("/select")
    @NoTokenAccess
    public Response select(Goods goods) {
        List<Goods> lstGoods = goodsService.select(goods);
        return Response.success(lstGoods);
    }

    /**
     * 根据主键查询详情
     */
    @GetMapping("/detail/{id}")
    @NoTokenAccess
    public Response selectById(@PathVariable Integer id){
        Goods goods = goodsService.selectById(id);
        return Response.success(goods);
    }


    /**
     * 新增数据
     */
    @SysOperation("新增电影数据")
    @PostMapping("/add")
    public Response addGoods(@RequestBody Goods goods){
        goodsService.add(goods);
        return Response.success();
    }

    /**
     * 修改数据
     */
    @SysOperation("修改电影数据")
    @PostMapping("/update")
    public Response update(@RequestBody Goods goods){
        goodsService.update(goods);
        return Response.success();
    }

    /**
     * 根据id删除
     */
    @SysOperation("删除电影数据")
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        goodsService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        goodsService.deleteBatch(ids);
        return Response.success();
    }
}

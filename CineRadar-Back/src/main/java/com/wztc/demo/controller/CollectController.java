package com.wztc.demo.controller;


import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.Collect;
import com.wztc.demo.service.CollectService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏Controller层  -> 对应表: collect
 */
@RestController
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    public Response selectByPage(Collect collect,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Collect> lstCollect = collectService.selectByPage(collect, pageNum, pageSize);
        PageInfo<Collect> pageInfo = new PageInfo<>(lstCollect);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<Collect>(total, lstCollect));
    }


    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody Collect collect){
        collectService.add(collect);
        return Response.success();
    }

    /**
     * 根据id删除数据
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        collectService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除数据
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        collectService.deleteBatch(ids);
        return Response.success();
    }
}
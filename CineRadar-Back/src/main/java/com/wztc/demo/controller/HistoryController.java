package com.wztc.demo.controller;


import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.PageCommon;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.History;
import com.wztc.demo.service.HistoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 足迹Controller层  -> 对应表: history
 */
@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    /**
     * 分页查询
     */
    @GetMapping("/selectByPage")
    @NoTokenAccess
    public Response selectByPage(History history,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        List<History> lstHistory = historyService.selectByPage(history, pageNum, pageSize);
        PageInfo<History> pageInfo = new PageInfo<>(lstHistory);
        long total = (int)pageInfo.getTotal();
        return Response.success(new PageCommon<History>(total, lstHistory));
    }


    /**
     * 新增数据
     */
    @PostMapping("/add")
    public Response add(@RequestBody History history){
        historyService.add(history);
        return Response.success();
    }

    /**
     * 根据id删除数据
     */
    @GetMapping("/delete/{id}")
    public Response deleteById(@PathVariable Integer id){
        historyService.deleteById(id);
        return Response.success();
    }

    /**
     * 批量删除数据
     */
    @PostMapping("/delete/batch")
    public Response deleteBatch(@RequestBody List<Integer> ids){
        historyService.deleteBatch(ids);
        return Response.success();
    }
}
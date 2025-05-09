package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.Response;
import com.wztc.demo.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计 Controller层  -> 对应表: 需要统计的表
 */
@RestController
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    private ChartService chartService;

    /**
     * 统计数据
     * @return
     */
    @GetMapping("/summary")
    public Response summary(){
        Map summaryData = chartService.getSummaryData();
        return Response.success(summaryData);
    }

    /**
     * 统计用户注册情况--折线图
     * @return
     */
    @GetMapping("/userRegister")
    @NoTokenAccess
    public Response userRegister(@RequestParam Map<String,String> dataMap){
        List<Map> chartData = chartService.userRegister(dataMap);
        return Response.success(chartData);
    }

    /**
     * 统计类型占比--饼状图
     * @return
     */
    @GetMapping("/cateCount")
    @NoTokenAccess
    public Response cateCount(){
        List<Map> chartData = chartService.cateCount();
        return Response.success(chartData);
    }

    /**
     * 统计演员电影演出--柱状图
     * @return
     */
    @GetMapping("/castCount")
    @NoTokenAccess
    public Response castCount(){
        List<Map> chartData = chartService.castCount();
        return Response.success(chartData);
    }

    /**
     * 统计各年份电影上映数量--折线图
     * @return
     */
    @GetMapping("/yearCount")
    @NoTokenAccess
    public Response yearCount(){
        List<Map> chartData = chartService.yearCount();
        return Response.success(chartData);
    }
}

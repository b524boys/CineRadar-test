package com.wztc.demo.service;

import com.wztc.demo.mapper.ChartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计 Service层
 */
@Service
public class ChartService {

    @Autowired
    private ChartMapper chartMapper;

    /**
     * 统计数据
     * @return
     */
    public Map getSummaryData() {
        Map<String,Integer> summaryData = new HashMap<>();
        summaryData.put("userCount", chartMapper.getUserCount());
        summaryData.put("goodsCount", chartMapper.getGoodsCount());
        summaryData.put("hitCount", chartMapper.getHitCount());
        summaryData.put("commentsCount", chartMapper.getCommentsCount());
        return summaryData;
    }

    /**
     * 统计用户注册情况
     * @return
     */
    public List<Map> userRegister(Map<String,String> dataMap){
        return chartMapper.userRegister(dataMap);
    }

    /**
     * 统计类型占比
     * @return
     */
    public List<Map> cateCount(){
        return chartMapper.cateCount();
    }

    /**
     * 统计演员电影演出--柱状图
     * @return
     */
    @GetMapping("/castCount")
    public List<Map> castCount(){
        return chartMapper.castCount();
    }

    /**
     * 统计各年份电影上映数量--折线图
     * @return
     */
    public List<Map> yearCount(){
        return chartMapper.yearCount();
    }
}

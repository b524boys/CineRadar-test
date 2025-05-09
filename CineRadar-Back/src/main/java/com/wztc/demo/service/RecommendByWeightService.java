package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import com.wztc.demo.entity.Goods;
import com.wztc.demo.mapper.GoodsMapper;
import com.wztc.demo.mapper.RecommendMapper;
import com.wztc.demo.util.EnvBeanUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *查询推荐商品
 **/
@Service
public class RecommendByWeightService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RecommendMapper recommendMapper;


    /**
     * 查询推荐商品
     */
    public List<Goods> queryRecommendGoods() {
        final List<Integer> recommendedGoodsLst = new ArrayList<>();
        String collectWeight = EnvBeanUtil.getString("recommend.weight.collection");
        String historyWeight = EnvBeanUtil.getString("recommend.weight.history");
        String commentWeight = EnvBeanUtil.getString("recommend.weight.comment");

        //查询最高权重的8条数据
        PageHelper.startPage(1, 8);
        List<Map<Object,Object>> recommendListByWeight = recommendMapper.recommendListByWeight(
                Integer.valueOf(collectWeight),
                Integer.valueOf(historyWeight),
                Integer.valueOf(commentWeight));

        recommendListByWeight.stream().forEach(maps -> {
            // Long 转 Integer
            Integer goodsId = maps.get("goods_id") instanceof Long ?
                    ((Long) maps.get("goods_id")).intValue() : (Integer) maps.get("goods_id");
            if (!recommendedGoodsLst.contains(goodsId)) {
                recommendedGoodsLst.add(goodsId);
            }
        });


        if(CollectionUtil.isNotEmpty(recommendedGoodsLst)){
            return goodsMapper.selectGoods(recommendedGoodsLst);
        }else{
            return null;
        }
    }
}

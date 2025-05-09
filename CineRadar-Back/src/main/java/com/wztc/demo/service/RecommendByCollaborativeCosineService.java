package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import com.wztc.demo.entity.Collect;
import com.wztc.demo.entity.Comments;
import com.wztc.demo.entity.Goods;
import com.wztc.demo.entity.History;
import com.wztc.demo.mapper.*;
import com.wztc.demo.mapper.*;
import com.wztc.demo.util.EnvBeanUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 协同过滤-余弦函数
 */
@Service
public class RecommendByCollaborativeCosineService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RecommendMapper recommendMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private HistoryMapper historyMapper;
    @Autowired
    private CommentsMapper commentsMapper;

    // userid, goodsId, weight
    private Map<Integer, Map<Integer, Double>> userGoodsWeight = new HashMap<>();
    private Map<Integer, Set<Integer>> userGoodsMap = new HashMap<>();

    /**
     * 查询推荐商品
     */
    public List<Goods> queryRecommendGoods(Integer userId) {
        final List<Integer> recommendedGoodsLst = new ArrayList<>();
        int maxRecommendations = 8;

        String collectWeight = EnvBeanUtil.getString("recommend.weight.collection");
        String historyWeight = EnvBeanUtil.getString("recommend.weight.history");
        String commentWeight = EnvBeanUtil.getString("recommend.weight.comment");

        if(null == userId){
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
        }else{
            // 协同过滤余弦函数
            userGoodsWeight = new HashMap<>();
            userGoodsMap = new HashMap<>();
            wrapUserGoodsWeight(userGoodsCollectWeight());
            wrapUserGoodsWeight(userGoodsHistoryWeight());
            wrapUserGoodsWeight(userGoodsCommentsWeight());
            recommendGoodsForUser(userId, maxRecommendations, recommendedGoodsLst);

            //推荐物品数量是8, 不满8个需要补全且物品不能重复
            if (recommendedGoodsLst.size() < maxRecommendations) {
                System.out.println(recommendedGoodsLst);
                // 查询 Goods表, 按照权重 查询 maxRecommendations 条数据
                PageHelper.startPage(1, maxRecommendations);
                List<Map<Object,Object>> recommendListByWeight = recommendMapper.recommendListByWeight(
                        Integer.valueOf(collectWeight),
                        Integer.valueOf(historyWeight),
                        Integer.valueOf(commentWeight));
                // 补充到maxRecommendations个
                recommendListByWeight.stream().forEach(maps -> {
                    // Long 转 Integer
                    Integer goodsId = maps.get("goods_id") instanceof Long ?
                            ((Long) maps.get("goods_id")).intValue() : (Integer) maps.get("goods_id");
                    if (!recommendedGoodsLst.contains(goodsId) && recommendedGoodsLst.size() < maxRecommendations) {
                        recommendedGoodsLst.add(goodsId);
                    }
                });
            }
        }

        if(CollectionUtil.isNotEmpty(recommendedGoodsLst)){
            return goodsMapper.selectGoods(recommendedGoodsLst);
        }else{
            return null;
        }
    }

    /**
     * 用户物品收藏权重
     */
    private Map<Integer,Map<Integer,Double>> userGoodsCollectWeight(){
        String collectWeight = EnvBeanUtil.getString("recommend.weight.collection");
        // 1.查询全部收藏数据
        List<Collect> collectList = collectMapper.select(null);
        //System.out.println(collectList);
        //初始化 所有的 收藏数据
        Map<Integer, Map<Integer, Double>> userGoodsCollectWeight = new HashMap<>();
        double collectWeightDouble = 1.0 * Integer.valueOf(collectWeight);
        collectList.forEach(collect -> {
            userGoodsCollectWeight.computeIfAbsent(collect.getUserId(), k -> new HashMap<>())
                    .merge(collect.getGoodsId(), 1*collectWeightDouble, Double::sum);
        });
        //System.out.println(userGoodsCollectWeight);
        return userGoodsCollectWeight;
    }

    /**
     * 用户物品历史访问记录权重
     */
    private Map<Integer,Map<Integer,Double>> userGoodsHistoryWeight(){
        String historyWeight = EnvBeanUtil.getString("recommend.weight.history");
        Map<Integer, Map<Integer, Double>> userGoodsHistoryWeight = new HashMap<>();
        double historyWeightDouble = 1.0 * Integer.valueOf(historyWeight);
        List<History> historyList = historyMapper.selectViewCountByHit();
        //System.out.println(historyList);
        historyList.forEach(historty->{
            userGoodsHistoryWeight.computeIfAbsent(historty.getUserId(), k -> new HashMap<>())
                    .merge(historty.getGoodsId(), historty.getHitCnt()*historyWeightDouble, Double::sum);
        });
        //System.out.println(userGoodsHistoryWeight);
        return userGoodsHistoryWeight;
    }

    /**
     * 用户物品评论权重
     */
    private Map<Integer,Map<Integer,Double>> userGoodsCommentsWeight(){
        String commentWeight = EnvBeanUtil.getString("recommend.weight.comment");
        Map<Integer, Map<Integer, Double>> userGoodsCommentsWeight = new HashMap<>();
        double commentWeightDouble = 1.0 * Integer.valueOf(commentWeight);
        List<Comments> commentList = commentsMapper.selectCommentsRate();
        //System.out.println(commentList);
        commentList.forEach(comment ->{
            userGoodsCommentsWeight.computeIfAbsent(comment.getUserId(), k -> new HashMap<>())
                    .merge(comment.getGoodsId(), comment.getRate()*commentWeightDouble, Double::sum);
        });
        //System.out.println(userGoodsCommentsWeight);
        return userGoodsCommentsWeight;
    }


    /**
     * 用户-物品权重(权重叠加)
     */
    private   Map<Integer, Map<Integer, Double>> wrapUserGoodsWeight(Map<Integer, Map<Integer, Double>> userGoodsWeightMap){
        // 1.为每个用户建立物品评分集合
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userGoodsWeightMap.entrySet()) {
            Integer userId = entry.getKey();
            Map<Integer, Double> goodsWeightMap = entry.getValue();
            if (!userGoodsWeight.containsKey(userId)) {
                userGoodsWeight.put(userId, new HashMap<>());
            }
            goodsWeightMap.entrySet().stream().forEach(goodsWeight -> {
                Integer goodsId = goodsWeight.getKey();
                userGoodsWeight.get(userId).put(goodsId, userGoodsWeight.get(userId).getOrDefault(goodsId,0.0) + goodsWeight.getValue());
            });
        }
        //System.out.println("物品评分集合"+userGoodsWeight);
        // 2.为每个用户建立物品集合
        userGoodsWeight.entrySet().stream().forEach(entry -> {
            Integer userId = entry.getKey();
            Map<Integer, Double> goodsWeight = entry.getValue();
            if (!userGoodsMap.containsKey(userId)) {
                userGoodsMap.put(userId, new HashSet<>());
            }
            userGoodsMap.get(userId).addAll(goodsWeight.keySet());
        });
        //System.out.println("物品集合"+userGoodsMap);
        return  userGoodsWeight;
    }

    private void recommendGoodsForUser(int targetUserId,int maxRecommendations,List<Integer> recommendGoods){
        Map<Integer, Double> similarityUserScores = new HashMap<>();

        // 1.计算目标用户与其他用户的相似度(余弦函数)
        for (Map.Entry<Integer,Set<Integer>> entry : userGoodsMap.entrySet()) {
            int userId = entry.getKey();
            if (userId != targetUserId) {
                // 余弦相似度计算
                double similarityScore = cosineSimilarity(userGoodsWeight.get(targetUserId), userGoodsWeight.get(userId));

                if (similarityScore > 0 ) {
                    similarityUserScores.put(userId, similarityScore);
                }
            }
        }

        // 2. 获取与目标用户最相似的n个用户
        Map<Integer, Double> sortedSimilarityUserScores =  getMostSimilarUsers(similarityUserScores, 5);

        // 3.预测物品的评分 (利用用户相似度和相似用户的物品权重进行加权获得最终评分 )
        Map<Integer, Double> predictGoodsRatings = predictGoodsRatingsByWeightAndSimilarityUserScores(targetUserId, sortedSimilarityUserScores);

        // 4.按照加权评分倒序获取前N个物品
        predictGoodsRatings.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(maxRecommendations) // 取前N个物品
                .forEachOrdered(entry -> {
                    recommendGoods.add(entry.getKey());
                });
    }

    /**
     * 1.计算目标用户与其他用户的相似度(余弦函数)
     */
    private double cosineSimilarity(Map<Integer, Double> targetGoodsWeight, Map<Integer, Double> compareGoodsWeight) {
        // 向量乘积
        double dotProduct = 0.0;
        // 目标向量模和比较向量模
        double normTarget = 0.0;
        double normCompare = 0.0;
        if(null == targetGoodsWeight){
            return 0.0;
        }

        // 找出两个用户共同评分的goodsIds  即交集
        Set<Integer> goodsIdSet = new HashSet<>(targetGoodsWeight.keySet());
        goodsIdSet.retainAll(compareGoodsWeight.keySet());

        //向量相乘
        for (Integer goodsId : goodsIdSet) {
            dotProduct += targetGoodsWeight.get(goodsId) * compareGoodsWeight.get(goodsId);
        }

        // 计算normTarget和normCompare的完整平方和
        for (Integer goodsId : targetGoodsWeight.keySet()) {
            normTarget += Math.pow(targetGoodsWeight.get(goodsId), 2);
        }
        for (Integer goodsId : compareGoodsWeight.keySet()) {
            normCompare += Math.pow(compareGoodsWeight.get(goodsId), 2);
        }

        if (normTarget == 0 || normCompare == 0) {
            return 0.0; // 如果没有共同评分的物品，返回0 (我觉得不对，应该是dotProduct=0)aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        }

        double similarityScore = dotProduct / (Math.sqrt(normTarget) * Math.sqrt(normCompare));
        return similarityScore;
    }

    /**
     *  2.获取与目标用户最相似的n个用户
     */
    private Map<Integer, Double>  getMostSimilarUsers(Map<Integer, Double> similarityUserScores , int n){
        Map<Integer, Double>  similarUsers = new HashMap<>();
        // 选择最相似的n个用户（实际应用中可能需要动态调整）
        similarityUserScores.entrySet().stream()
                // 倒叙排序
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .forEach(entry -> {
                    similarUsers.put(entry.getKey(), entry.getValue());
                });
        return similarUsers;
    }

    /**
     * 3.利用用户相似度和物品权重进行加权获得最终物品评分
     */
    private Map<Integer, Double> predictGoodsRatingsByWeightAndSimilarityUserScores(Integer targetUserId, Map<Integer, Double> sortedSimilarityUserScores) {
        // 待推荐的物品
        Set<Integer> recommendedGoodsIdsSet = new HashSet<>();
        sortedSimilarityUserScores.entrySet().stream().forEach(entry -> {
            int similarUserId = entry.getKey();
            Set<Integer> goodsIdsSet = userGoodsMap.get(similarUserId);
            // 将相似用户的物品添加到推荐列表中，排除目标用户中的物品
            for (int goodsId : goodsIdsSet) {
                if (!userGoodsMap.get(targetUserId).contains(goodsId)) {
                    recommendedGoodsIdsSet.add(goodsId);
                }
            }
        });

        Map<Integer, Double> predictedGoodsRatings = new HashMap<>();
        recommendedGoodsIdsSet.stream().forEach(goodsId -> {
            // 利用用户相似度和物品权重进行加权获得最终评分
            double goodsRatingSum = 0.0;
            double similaritySum = 0.0;
            for (Map.Entry<Integer, Double> entry : sortedSimilarityUserScores.entrySet()) {
                int similarUserId = entry.getKey();
                double similarity = entry.getValue();
                similaritySum += similarity;
                if(null != userGoodsWeight.get(similarUserId).get(goodsId)){
                    double weight = userGoodsWeight.get(similarUserId).get(goodsId);
                    goodsRatingSum += similarity * weight;
                }
            }
            if (goodsRatingSum == 0) {
                predictedGoodsRatings.put(goodsId, 0.0);
            }else {
                predictedGoodsRatings.put(goodsId, goodsRatingSum / similaritySum);
            }
        });

        return predictedGoodsRatings;
    }
}

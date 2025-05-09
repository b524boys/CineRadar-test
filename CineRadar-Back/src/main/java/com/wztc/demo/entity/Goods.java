package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    private Integer id;
    //电影名称
    private String goodsName;
    private String searchGoodsName;
    //电影分类名称
    private String cateName;
    //电影分类名称List
    private List<String>  cateNameList;
    //封面
    private String cover;
    //简介
    private String introduce;
    //点击次数
    private Integer hits;
    //视频Url
    private String videoUrl;
    //创建时间
    private String createTime;
    //导演
    private String director;
    //演员
    private String casts;
    //上映日期
    private String year;
    //国家
    private String country;
    //时长
    private Integer duration;
    //综合评分
    private Double rating;
}

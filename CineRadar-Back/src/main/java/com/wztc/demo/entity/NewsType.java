package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsType {
    private Integer id;
    private String typeName;
    private String createTime;

    /**
     * 扩展属性, 便于显示每个分类下的所有新闻公告
     * 此属性用于首页的通知公告
     */
    private List<News> lstNews;
}

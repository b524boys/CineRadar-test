package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    private Integer id;
    private Integer userId;
    private String userName;
    private String nickName;
    private String headImg;
    private String replyUserId;
    private String replyUserName;
    private String replyNickName;
    private String replyHeadImg;
    private Integer goodsId;
    private String goodsName;
    private String content;
    private Integer parentId;
    private Integer level;
    private Double rate;
    private String createTime;
    private List<Comments> children;
    //关联附件
    private List<Attach> attachList;
}
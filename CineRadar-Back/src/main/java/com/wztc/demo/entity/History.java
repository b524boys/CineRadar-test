package com.wztc.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private Integer id;
    private Integer goodsId;
    private String goodsName;
    private String cover;
    private Integer userId;
    private String userName;
    private String nickName;
    private String createTime;

    @JsonIgnore
    // 浏览次数
    private Integer hitCnt;
}
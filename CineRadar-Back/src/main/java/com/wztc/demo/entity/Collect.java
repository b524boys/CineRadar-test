package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collect {
    private Integer id;
    private Integer goodsId;
    private String goodsName;
    private String cover;
    private Integer userId;
    private String userName;
    private String nickName;
    private String createTime;

}

package com.wztc.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseInfo{
    private Integer id;
    private String userName;
    private String password;
    private String nickName;
    private String sex;
    private String phone;
    private String email;
    private String headImg;
    private String createTime;
}

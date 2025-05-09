package com.wztc.demo.entity.AiChat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    //电影名
    private String name;
    //电影id
    private Integer id;
    //封面
    private String cover;
    //推荐理由
    private String reason;
    //跳转链接
    private String url;

//    无参构造函数（Fastjson 需要）
//    public Movie() {}
//
//    带参构造函数
//    public Movie(String name, String reason) {
//        this.name = name;
//        this.reason = reason;
//    }

}


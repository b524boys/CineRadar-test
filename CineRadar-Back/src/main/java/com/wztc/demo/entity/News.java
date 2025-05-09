package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private Integer id;
    private String title;
    private Integer newsTypeId;
    private String typeName;
    private String content;
    private String cover;
    private Integer hits;
    private Integer top;
    private String publisher;
    private String createTime;
}

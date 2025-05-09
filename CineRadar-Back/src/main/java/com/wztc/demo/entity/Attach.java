package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attach {
    private Integer id;
    private String tableName;
    private Integer tableId;
    private String attachFile;
    private String createTime;
}
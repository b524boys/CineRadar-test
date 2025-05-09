package com.wztc.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysLog {
    private Integer id;
    private String name;
    private String operation;
    private String method;
    private String params;
    private Long respTime;
    private String ip;
    private String createTime;
}

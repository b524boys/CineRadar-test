package com.wztc.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ElasticSearchResult<T> {
    private long total;
    private List<T> items;

    public long getTotal() {
        return total;
    }

    public List<T> getItems() {
        return items;
    }

    // 判断列表内容是否为空方法
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}

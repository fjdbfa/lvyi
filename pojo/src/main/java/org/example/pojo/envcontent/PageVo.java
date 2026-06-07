package org.example.pojo.envcontent;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    private Integer total;
    private List<T> data;
}

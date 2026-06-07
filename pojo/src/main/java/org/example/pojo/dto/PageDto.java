package org.example.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class PageDto implements Serializable {

    private Integer currentPage;
    private Integer pageSize;
}

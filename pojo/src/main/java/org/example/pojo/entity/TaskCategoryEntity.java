package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "task_category",autoResultMap = true)
public class TaskCategoryEntity {
    private Integer id;
    private String title;
    private String accomplishText;
    private String categoryType;
    private Integer isDeleted;
}

package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "task_diy", autoResultMap = true)
public class TaskDIY {
    private Long id;
    private String title;
    private String materials;
    private String tools;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> steps;
    private String detailUrl;
    private String image;
}
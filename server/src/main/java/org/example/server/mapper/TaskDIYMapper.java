package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.TaskDIY;

@Mapper
public interface TaskDIYMapper extends BaseMapper<TaskDIY> {
    // 继承 BaseMapper 后，已自带 selectPage 等方法，无需额外编写
}
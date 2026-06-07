package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.pojo.entity.TaskCategoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMapper extends BaseMapper<TaskCategoryEntity> {
}

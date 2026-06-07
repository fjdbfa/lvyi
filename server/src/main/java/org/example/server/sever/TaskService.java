package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.dto.PageDto;
import org.example.pojo.entity.TaskCategoryEntity;
import org.example.pojo.envcontent.PageVo;
import org.example.pojo.envcontent.TaskListDataVO;

public interface TaskService extends IService<TaskCategoryEntity> {
    Result<TaskListDataVO> getTaskList();

   ;
}

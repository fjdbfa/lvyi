package org.example.server.sever;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.dto.PageDto;
import org.example.pojo.entity.TaskDIY;
import org.example.pojo.envcontent.PageVo;

public interface TaskDIYService extends IService<TaskDIY> {
    /**
     * 分页查询TaskDIY列表
     * @param pageDto 分页参数
     * @return 分页结果
     */
    Result<PageVo<TaskDIY>> getTaskDIYList(PageDto pageDto);
}
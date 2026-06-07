package org.example.server.sever.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.dto.PageDto;
import org.example.pojo.entity.TaskDIY;
import org.example.pojo.envcontent.PageVo;
import org.example.server.mapper.TaskDIYMapper;
import org.example.server.sever.TaskDIYService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskDIYServiceImpl extends ServiceImpl<TaskDIYMapper, TaskDIY> implements TaskDIYService {
    
    private final TaskDIYMapper taskDIYMapper;

    /**
     * 分页查询TaskDIY列表
     * @param pageDto 分页参数
     * @return 分页结果
     */
    @Override
    public Result<PageVo<TaskDIY>> getTaskDIYList(PageDto pageDto) {
        try {
            // 创建分页对象
            Page<TaskDIY> page = new Page<>(pageDto.getCurrentPage(), pageDto.getPageSize());
            log.info("分页查询TaskDIY列表");
            // 执行分页查询
            IPage<TaskDIY> result = taskDIYMapper.selectPage(page, null);
            
            // 封装返回结果
            PageVo<TaskDIY> pageVo = new PageVo<>();
            pageVo.setTotal(Long.valueOf(result.getTotal()).intValue());
            pageVo.setData(result.getRecords());
            
            return Result.success(pageVo);
        } catch (Exception e) {
            log.error("分页查询TaskDIY列表失败", e);
            return Result.error("查询失败");
        }
    }
}
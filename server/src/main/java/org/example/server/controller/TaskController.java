package org.example.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.dto.PageDto;
import org.example.pojo.entity.TaskDIY;
import org.example.pojo.envcontent.PageVo;
import org.example.pojo.envcontent.TaskListDataVO;
import org.example.server.sever.TaskService;
import org.example.server.sever.TaskDIYService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
@Slf4j
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskDIYService taskDIYService;
    
    @GetMapping("/taskList")
    public Result<TaskListDataVO> taskList(){
        log.info("获取任务列表");
        try {
            return taskService.getTaskList();
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            return Result.error("获取任务列表失败");
        }

    }
    
    @PostMapping("/DIY")
    public Result<PageVo<TaskDIY>> getTaskDIYList(@RequestBody PageDto pageDto) {
        return taskDIYService.getTaskDIYList(pageDto);
    }
}
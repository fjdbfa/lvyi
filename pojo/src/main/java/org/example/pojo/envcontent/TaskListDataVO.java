package org.example.pojo.envcontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListDataVO {
    private List<TaskItemVO> commonList;   // 普通任务
    private List<TaskItemVO> notSpotList;  // 未打卡任务
    private List<TaskItemVO> hideList;     // 隐藏任务
}

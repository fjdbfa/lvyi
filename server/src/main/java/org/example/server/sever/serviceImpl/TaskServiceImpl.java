package org.example.server.sever.serviceImpl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ai4j.openai4j.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.entity.TaskCategoryEntity;
import org.example.pojo.entity.TaskDIY;
import org.example.pojo.envcontent.TaskItemVO;
import org.example.pojo.envcontent.TaskListDataVO;
import org.example.pojo.envcontent.PageVo;
import org.example.server.mapper.TaskMapper;
import org.example.server.sever.TaskService;


import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.common.constant.RedisConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskCategoryEntity> implements TaskService {
    public static String GLOBAL = "global";

     private final TaskMapper taskMapper;
    // 使用 StringRedisTemplate 处理字符串 JSON
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    @Override
    public Result<TaskListDataVO> getTaskList() {

        log.info("开始获取任务列表数据");

        // 检查用户是否已登录
//        Integer userId =  UserHolder.getUser().getId();;
//        if (userId == null) {
//          throw new BusinessException("用户未登录");
//        }
        String redisKey = REDIS_TASK_KEY_PREFIX + GLOBAL;

        // 1. 先尝试从 Redis 获取
        log.debug("尝试从Redis获取任务列表缓存，key: {}", redisKey);
        TaskListDataVO cached = getFromRedis(redisKey);
        if (cached != null) {
            log.info("成功从Redis获取任务列表缓存，key: {}", redisKey);
            return Result.success(cached);
        }
        log.debug("Redis缓存未命中，准备查询数据库");

        // 2. 缓存未命中，查询数据库

        if (taskMapper == null) {
            log.error("TaskMapper未注入");
            throw new RuntimeException("TaskMapper未注入");
        }
        
        log.debug("开始查询数据库中的任务列表数据");
        List<TaskCategoryEntity> tasks = taskMapper.selectList(
            new LambdaQueryWrapper<TaskCategoryEntity>()
                .eq(TaskCategoryEntity::getIsDeleted, 0) // 只查启用状态
        );
        log.info("数据库查询完成，共找到 {} 条任务数据", tasks.size());
        
        // 3. 转换并分组
        log.debug("开始转换任务数据并进行分组");
        TaskListDataVO vo = convertToVO(tasks);
        TaskListDataVO taskListDataVO = cacheToRedis(vo);

        log.info("任务列表数据处理完成，已缓存到Redis");
        return Result.success(taskListDataVO);
    }

    /**
     * 从 Redis 获取缓存数据（使用手动 JSON 反序列化）
     */
//    private TaskListDataVO getFromRedis(String redisKey) {
//        try {
//            String json = stringRedisTemplate.opsForValue().get(redisKey);
//            if (json != null && json.trim().startsWith("{")) {
//                return objectMapper.readValue(json, TaskListDataVO.class);
//            }
//        } catch (Exception e) {
//            log.error("反序列化失败", e);
//        }
//        return null;
//    }
//
//    /**
//     * 将数据缓存到 Redis（使用手动 JSON 序列化）
//     */
//    private TaskListDataVO cacheToRedis(TaskListDataVO original) {
//        String redisKey = REDIS_TASK_KEY_PREFIX + GLOBAL;
//
//        TaskListDataVO toCache = new TaskListDataVO(
//                randomLimit(original.getCommonList(), 5),
//                randomLimit(original.getNotSpotList(), 5),
//                randomLimit(original.getHideList(), 5)
//        );
//
//        try {
//            String json = objectMapper.writeValueAsString(toCache);
//            stringRedisTemplate.opsForValue().set(redisKey, json, CACHE_TTL);
//            log.info("✅ 缓存成功: {}", json); // 日志确认格式
//        } catch (Exception e) {
//            log.error("缓存失败", e);
//        }
//        return toCache;
//    }
/**
 * 从给定的列表中随机选取指定数量的元素，返回一个新的列表
 * @param list 原始列表，不能为null
 * @param limit 需要返回的元素数量
 * @param <T> 列表中元素的类型
 * @return 包含随机选取的元素的新列表，如果输入列表为空或null则返回空列表
 */
    /**
     * 从 Redis 获取缓存数据，并反序列化为 TaskListDataVO
     */
    private TaskListDataVO getFromRedis(String redisKey) {
        try {
            String json = stringRedisTemplate.opsForValue().get(redisKey);
            if (json != null && !json.trim().isEmpty()) {
                // 必须使用 TypeReference 来保留泛型信息
                Map<String, List<TaskItemVO>> map = objectMapper.readValue(json,
                        new TypeReference<Map<String, List<TaskItemVO>>>() {});

                // 从 Map 构造回 TaskListDataVO
                return new TaskListDataVO(
                        map.getOrDefault("commonList", Collections.emptyList()),
                        map.getOrDefault("notSpotList", Collections.emptyList()),
                        map.getOrDefault("hideList", Collections.emptyList())
                );
            }
        } catch (Exception e) {
            log.error("反序列化失败，Redis key: {}", redisKey, e);
        }
        return null;
    }

    /**
     * 将数据缓存到 Redis（先转为 Map<String, List<TaskItemVO>> 再序列化）
     */
    private TaskListDataVO cacheToRedis(TaskListDataVO original) {
        String redisKey = REDIS_TASK_KEY_PREFIX + GLOBAL;
        ;
        // 先对原始列表做 randomLimit 处理
        Map<String, List<TaskItemVO>> toCache = new HashMap<>();
        toCache.put("commonList", randomLimit(original.getCommonList(), 5));
        toCache.put("notSpotList", randomLimit(original.getNotSpotList(), 5));
        toCache.put("hideList", randomLimit(original.getHideList(), 5));

        try {
            // 序列化 Map 而不是 VO
            String json = objectMapper.writeValueAsString(toCache);
            stringRedisTemplate.opsForValue().set(redisKey, json, java.time.Duration.ofSeconds(CACHE_TTL_SECONDS));
            log.info("✅ 缓存成功: {}", json);
        } catch (Exception e) {
            log.error("缓存失败", e);
        }

        // 返回一个新的 TaskListDataVO（用于后续业务逻辑使用）
        return new TaskListDataVO(
                toCache.get("commonList"),
                toCache.get("notSpotList"),
                toCache.get("hideList")
        );
    }




    private <T> List<T> randomLimit(List<T> list, int limit) {
        // 检查输入列表是否为null或空，如果是则返回空列表
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        // 创建原始列表的副本，以避免修改原始列表
        List<T> copy = new ArrayList<>(list);
        // 打乱副本中元素的顺序
        Collections.shuffle(copy);
        // 限制返回的元素数量为limit，并将结果收集到新列表中返回
        return copy.stream().limit(limit).collect(Collectors.toList());
    }
    /**
     * 将数据库实体转换为 VO 并按类型分组
     */
    private TaskListDataVO convertToVO(List<TaskCategoryEntity> entities) {
        TaskListDataVO vo = new TaskListDataVO();

        Map<String, List<TaskCategoryEntity>> grouped = entities.stream()
                .collect(Collectors.groupingBy(TaskCategoryEntity::getCategoryType));

        vo.setCommonList(toItemVOList(grouped.getOrDefault("common", Collections.emptyList())));
        vo.setNotSpotList(toItemVOList(grouped.getOrDefault("not_spot", Collections.emptyList())));
        vo.setHideList(toItemVOList(grouped.getOrDefault("hide", Collections.emptyList())));

        return vo;
    }
/**
 * 将任务分类实体列表转换为任务项视图对象列表
 * 该方法通过Stream流对实体列表进行处理，将每个实体转换为对应的视图对象
 *
 * @param entities 任务分类实体列表
 * @return 任务项视图对象列表，包含ID、标题和完成文本信息
 */
    private List<TaskItemVO> toItemVOList(List<TaskCategoryEntity> entities) {
    // 使用Stream流处理实体列表，将每个实体映射为视图对象
        return entities.stream().map(e -> {
        // 创建新的任务项视图对象
            TaskItemVO item = new TaskItemVO();
        // 设置视图对象的ID，来源于实体的ID
            item.setId(e.getId());
        // 设置视图对象的标题，来源于实体的标题
            item.setTitle(e.getTitle());
        // 设置视图对象的完成文本，来源于实体的完成文本
            item.setAccomplishText(e.getAccomplishText());
        // 返回转换后的视图对象
            return item;
        }).collect(Collectors.toList()); // 将流处理结果收集为List并返回
    }
    

}
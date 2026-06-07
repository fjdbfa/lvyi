package org.example.server.sever.serviceImpl;

import org.example.common.result.Result;
import org.example.common.utils.PageUtil;
import org.example.server.sever.EnvContentSucessStoriesService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.server.mapper.envContentSucessStoriesMapper;
import org.example.pojo.envcontent.envContentSucessStoriesData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class EnvContentSucessStoriesServiceImpl extends ServiceImpl<envContentSucessStoriesMapper, envContentSucessStoriesData> implements EnvContentSucessStoriesService {
    private PageUtil page;
    public void page(PageUtil pageUtil) {
        this.page=pageUtil;
    }
    @Override
    public Result getSucessStoriesData(Integer pageNo, Integer pageSize) {
        List<envContentSucessStoriesData> records = page.queryPage(
                pageNo,
                pageSize,
                (page) -> this.page(page, null) // 无查询条件，直接分页查询全表

        );
        return Result.success(records);
    }
    @Override
    public Result getSucessStoriesRandomData() {
        long count = this.baseMapper.selectCount(null);
        List<envContentSucessStoriesData> records = new ArrayList<envContentSucessStoriesData>();
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(i);
        }
        // 随机打乱列表
        Collections.shuffle(list);

        // 检查列表是否为空
        if (list.isEmpty()) {
            return Result.success(records);
        }

        Random r = new Random();
        // 确保不会超过列表大小
        int limit = Math.min(4, list.size());
        for(int i=0;i<limit;i++){
            records.add(getById(list.get(i)));
        }


        return Result.success(records);
    }
}

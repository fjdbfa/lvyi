package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.envcontent.envContentSucessStoriesData;

public interface EnvContentSucessStoriesService extends IService<envContentSucessStoriesData> {
    Result  getSucessStoriesData(Integer pageNo, Integer pageSize);

    Result getSucessStoriesRandomData();
}

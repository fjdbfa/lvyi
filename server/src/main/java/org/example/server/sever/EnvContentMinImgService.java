package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.envcontent.envContentMinImgData;

import java.util.List;

public interface EnvContentMinImgService extends IService<envContentMinImgData> {
    List<envContentMinImgData> page(Integer pageNo, Integer pageSize);
}

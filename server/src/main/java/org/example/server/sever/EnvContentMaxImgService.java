package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.envcontent.envContentMaxImgData;

import java.util.List;

public interface EnvContentMaxImgService extends IService<envContentMaxImgData> {
     List<envContentMaxImgData> page(Integer pageNo, Integer pageSize);
}

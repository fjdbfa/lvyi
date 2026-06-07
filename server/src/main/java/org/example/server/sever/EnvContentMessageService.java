package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.envcontent.envContentMessageData;

public interface EnvContentMessageService extends IService<envContentMessageData> {
    Result getMessageById(String x);
}

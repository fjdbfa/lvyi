package org.example.server.sever.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.result.Result;
import org.example.server.mapper.envContentMessageDataMapper;
import org.example.server.sever.EnvContentMessageService;
import org.example.pojo.envcontent.envContentMessageData;
import org.springframework.stereotype.Service;

@Service
public class EnvContentMessageServiceImpl extends ServiceImpl<envContentMessageDataMapper, envContentMessageData> implements EnvContentMessageService {
    @Override
    public Result getMessageById(String x) {
        envContentMessageData messageData = lambdaQuery().eq(envContentMessageData::getType, x).one();
        if (messageData == null) {
            return Result.error("消息不存在");
        }
        return Result.success(messageData);
    }
}

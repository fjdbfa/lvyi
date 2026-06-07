package org.example.server.sever;

import org.example.common.result.Result;
import org.example.pojo.envcontent.ChatMessageListVo;

import java.util.List;

public interface ChatService {
    String chat(String message, Long userId);
     void clearMemory(String boxId);

    Result<List<ChatMessageListVo>> getHistory(Long userId);
}

// src/main/java/com/example/secondhandvaluator/service/PictureAi.java
package org.example.server.AIinterface;


import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.example.pojo.entity.SaleRecord;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "opAiChatModel"
        )

public interface PictureAi {

    @SystemMessage("""
                请根据用户提供的图片和/或描述，识别以下信息：
                - 物品类别（如手机、笔记本）
                - 品牌
                - 型号
                - 成色（如9成新）
                - 明显瑕疵
                
                输出格式：{"category":"...","brand":"...","model":"...","condition":"...","defects":"..."}
                """)
    SaleRecord Picturesque(UserMessage userMessage);
}
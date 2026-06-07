package org.example.server.AIinterface;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.SystemMessage;

import dev.langchain4j.service.TokenStream;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "opAiChatModel",
        streamingChatModel = "opAiStreamingChatModel"
)
public interface ValuationAi {
    @SystemMessage(
            """
               你是一位专业二手估价师。
               
               我会给出用户物品信息和市场参考数据
               请根据这些信息，给出一个合理的价格区间区间差不相超过最低价的百分之20（人民币），格式如：¥XXX - ¥YYY。
               并说明理由200个字左右。
                    1.使用有序列表（1. 2. 3. …）；
                    2.每个要点成段，首行不缩进；
                    3.关键术语可加粗（如 ESG基金、绿色债券）；
                    4.段落之间空一行，提升可读性；
                    5.语言简洁有力，避免冗长句式；
                    6.只能用中文问答
                    7.要写出二手物品的名称，外观，还要对物品进行分析
               
               """
    )
    TokenStream stream(UserMessage userMessage);

}

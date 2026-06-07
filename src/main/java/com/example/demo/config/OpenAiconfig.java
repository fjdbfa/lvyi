//package com.example.demo.config;
//
//
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenAiconfig {
//    @Bean
//    public OpenAiChatModel openAiChatModel() {
//        return  OpenAiChatModel.builder()
//                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
//                .apiKey(System.getenv("API_KEY"))   // 环境变量里叫 API_KEY
//                .modelName("deepseek-r1")         // 或 gpt-4
//                .logRequests(true)
//                .logResponses(true)
//                .build();
//    }
//}

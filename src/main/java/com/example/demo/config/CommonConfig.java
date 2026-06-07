//package com.example.demo.config;
//
//import com.example.demo.aiservice.ConsultantService;
//import dev.langchain4j.model.chat.ChatLanguageModel;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import dev.langchain4j.service.AiServices;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class CommonConfig {
//    @Autowired
//    OpenAiChatModel model;
//    @Bean
//    public ConsultantService consultantService(){
//        return AiServices.create(ConsultantService.class, model);
//
//    }
//}

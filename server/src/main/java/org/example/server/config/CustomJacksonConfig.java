package org.example.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.langchain4j.data.message.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class CustomJacksonConfig {

    @Bean
    @Qualifier("chatMemoryObjectMapper")
    public ObjectMapper chatMemoryObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. 注册JavaTimeModule以支持Java 8日期时间类型
        mapper.registerModule(new JavaTimeModule());
        
        // 2. 注册 ChatMessage 的序列化器和反序列化器
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ChatMessage.class, new ChatMessageDeserializer());
        module.addSerializer(ChatMessage.class, new ChatMessageSerializer());
        mapper.registerModule(module);

        // 3. 配置日期格式
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper;
    }

    /**
     * 默认的 ObjectMapper，用于其他业务
     * 使用 @Primary 确保其他业务使用这个默认配置
     */
    @Bean
    @Primary
    public ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();



        // 注册 Java 8 日期时间模块
        mapper.registerModule(new JavaTimeModule());


        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper;
    }


}

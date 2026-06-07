// src/main/java/com/example/secondhandvaluator/service/ValuationServiceImpl.java
package org.example.server.sever.serviceImpl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.data.message.AiMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.common.utils.AliyunOssUtils;
import org.example.pojo.dto.ValuationRequestDTO;
import org.example.pojo.entity.AppraisalInformation;
import org.example.pojo.entity.SaleRecord;
import org.example.pojo.envcontent.ValuationResultVo;
import org.example.server.AIinterface.PictureAi;
import org.example.server.mapper.AppraisalInformationMapper;
import org.example.server.sever.ValuationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ValuationServiceImpl implements ValuationService {
    private final ObjectMapper objectMapper;
    private final OpenAiStreamingChatModel streamingChatModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final AliyunOssUtils aliyunOssUtils;
    private final AppraisalInformationMapper appraisalInformationMapper;

    public ValuationServiceImpl(ObjectMapper objectMapper,
                               @Qualifier("opAiStreamingChatModel") OpenAiStreamingChatModel streamingChatModel,
                               EmbeddingStore<TextSegment> embeddingStore,
                               AliyunOssUtils aliyunOssUtils,
                               AppraisalInformationMapper appraisalInformationMapper) {
        this.objectMapper = objectMapper;
        this.streamingChatModel = streamingChatModel;
        this.embeddingStore = embeddingStore;
        this.aliyunOssUtils = aliyunOssUtils;
        this.appraisalInformationMapper = appraisalInformationMapper;
    }

    private static final String VALUATION_SYSTEM_PROMPT = """
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

               """;

    public SseEmitter estimatePrice(Long id) throws IOException {
        List<Content> parts = new ArrayList<>();
        AppraisalInformation appraisalInformation = appraisalInformationMapper.selectById(id);
        log.info("开始处理图片和描述,{}", appraisalInformation);
        List<String> images = objectMapper.readValue(appraisalInformation.getJsonData(), new TypeReference<List<String>>() {
        });

        if(StringUtils.hasText(appraisalInformation.getDescription())) {
            parts.add(TextContent.from(appraisalInformation.getDescription()));
        }
        // 处理图片：OSS 公网可读时，直接传 image_url（避免下载/转码，速度更快）
        for (String imageUrl : images) {
            try {
                if (!StringUtils.hasText(imageUrl)) {
                    continue;
                }
                parts.add(ImageContent.from(imageUrl));
                log.info("添加图片(oss url)成功, url={}", imageUrl);
            } catch (Exception e) {
                log.error("添加图片失败, url={}", imageUrl, e);
            }
        }

        UserMessage userMessage = new UserMessage(parts);
        log.info("开始流式对话");
        SseEmitter emitter = new SseEmitter(60_000L);

        List<dev.langchain4j.data.message.ChatMessage> messages = List.of(
                SystemMessage.from(VALUATION_SYSTEM_PROMPT),
                userMessage
        );

        streamingChatModel.generate(messages, new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                try {
                    emitter.send(token);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                log.info("✅ 流完成");
                emitter.complete();
            }

            @Override
            public void onError(Throwable error) {
                emitter.completeWithError(error);
            }
        });

        return emitter;
    }

    @Override
    public Result getmultipart( ValuationRequestDTO requestDTO) throws JsonProcessingException {
        AppraisalInformation appraisalInformation = new AppraisalInformation();
        appraisalInformation.setDescription(requestDTO.getDescription());
        List<String> images = new ArrayList<>();
        for (MultipartFile image : requestDTO.getImages()) {
            images.add(aliyunOssUtils.uploadImage(image));
        }
        appraisalInformation.setJsonData(objectMapper.writeValueAsString(images));
        appraisalInformationMapper.insert(appraisalInformation);
       log.info("开始多目标评估{}" ,appraisalInformation.getId());
        return Result.success(appraisalInformation.getId());
    }
//public ValuationResultVo estimatePrice(String description, MultipartFile image) throws IOException {
//    List<Content> parts = new ArrayList<>();
//
//    if (StringUtils.hasText(description)) {
//        parts.add(TextContent.from(description));
//    }
//            if (image != null && !image.isEmpty()) {
//            String mimeType = image.getContentType();
//            if (mimeType == null || !mimeType.startsWith("image/")) {
//                mimeType = "image/jpeg"; // 默认设置为 image/jpeg 或者根据需要选择其他类型如 image/png
//            }
//
//            byte[] imageBytes = image.getBytes();
//            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//
//            // 仅添加 Base64 编码的图像数据，不包含 data URL 前缀
//            parts.add(ImageContent.from(base64Image));
//        }
//
//    if (parts.isEmpty()) {
//        throw new IllegalArgumentException("必须提供图片或描述");
//    }
//
//
//    UserMessage userMsg = UserMessage.from(parts);
//    SaleRecord attrs= aiService.Picturesque(userMsg);
//    // Step 3: 构造检索查询文本
//    String queryText = String.format("%s %s %s %s",
//            attrs.getPrice(),
//            attrs.getBrand(),
//            attrs.getModel(),
//            attrs.getCondition());
//    // Step 4: RAG 检索
//    List<String> relevantRecords = embeddingStore.findRelevant(queryText, 3).stream()
//            .map(match -> match.embedded().text())
//            .toList();
//
//    String marketContext = relevantRecords.isEmpty() ?
//            "无近期相似商品成交记录。" :
//            "近期相似商品成交记录：\n" + String.join("\n", relevantRecords);
//
//
//
//
//    ValuationResultVo response = valuationAi.estimatePrice(objectMapper.writeValueAsString(attrs) + "\n近期相似商品成交记录" + marketContext);
//    try {
//        return response;
//    } catch (Exception e) {
//        return new ValuationResultVo("¥500 - ¥1500", "模型输出解析失败，使用默认估价范围。" + (relevantRecords.isEmpty() ? "（无市场数据）" : ""));
//    }
//}
}
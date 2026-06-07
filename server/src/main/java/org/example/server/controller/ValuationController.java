// src/main/java/com/example/secondhandvaluator/controller/ValuationController.java
package org.example.server.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.dto.ValuationRequestDTO;
import org.example.pojo.envcontent.ValuationResultVo;
import org.example.server.sever.ValuationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/valuation")
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ValuationController {

    private final ValuationService valuationService;



    @PostMapping("/multipart")
    public Result<Long> multitargeting( @ModelAttribute ValuationRequestDTO requestDTO) throws JsonProcessingException {
        log.info("开始多目标评估{}" ,requestDTO);
      return valuationService.getmultipart(requestDTO);
    }
    @GetMapping(value = "/stream/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter estimatePrice(
           @PathVariable Long id) throws IOException {


        return valuationService.estimatePrice(id);
    }
}
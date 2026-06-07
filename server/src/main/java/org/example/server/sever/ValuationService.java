package org.example.server.sever;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.common.result.Result;
import org.example.pojo.dto.ValuationRequestDTO;
import org.example.pojo.envcontent.ValuationResultVo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
public interface ValuationService {
/**
 * 估算价格的接口方法
 * @param Long 参数类型为Long，可能表示商品ID或其他需要估算价格的标识符
 * @return ValuationResultVo 返回一个估值结果对象，包含价格估算相关信息
 * @throws IOException 可能抛出IO异常，表示在获取价格估算过程中发生输入输出错误
 */
    SseEmitter estimatePrice(Long id) throws IOException;

    Result<Long> getmultipart( ValuationRequestDTO requestDTO) throws JsonProcessingException;
}

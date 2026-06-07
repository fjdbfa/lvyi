// src/main/java/com/example/secondhandvaluator/dto/ValuationRequestDTO.java
package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuationRequestDTO {
    private String description;
    private List<MultipartFile> images; // 用于表单上传


}
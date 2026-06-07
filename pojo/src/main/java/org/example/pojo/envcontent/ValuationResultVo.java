package org.example.pojo.envcontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuationResultVo {
    private String estimatedPriceRange; // e.g., "¥800 - ¥1200"
    private String reasoning;           // 估价理由

}
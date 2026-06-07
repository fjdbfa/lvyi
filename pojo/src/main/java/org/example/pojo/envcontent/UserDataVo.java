package org.example.pojo.envcontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataVo {
    private Long greenPoints;   // 或 Integer，根据实际数值范围选择
    private Long followCount;   // 通常关注数可能较大，建议用 Long


}
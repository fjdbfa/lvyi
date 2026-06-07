package org.example.pojo.envcontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreativeListDataVo {

    private Long id;
    private Long userId;
    private String img;
    private String type;
    private String detail;


}
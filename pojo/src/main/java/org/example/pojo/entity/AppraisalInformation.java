package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@TableName("appraisal_information")
public class AppraisalInformation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("json_data")
    @JsonRawValue  // 保持 JSON 原始格式输出（可选）
    private String jsonData;  // 也可以用 JsonNode，见下方说明

    @TableField("description")
    private String description;
}





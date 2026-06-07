package org.example.pojo.envcontent;

import lombok.Data;
import org.example.pojo.dto.CommentDTO;

import java.util.List;
@Data
public class TotalCommentVo {
    private Integer total;
    private List<CommentDTO> data;
}

package org.example.server.sever;

import org.example.common.result.Result;
import org.example.pojo.dto.SendCommentDataDto;
import org.example.pojo.dto.SubmitCreativeCenterDataDto;
import org.example.pojo.entity.ForumChoiceness;
import org.example.pojo.envcontent.CreativeListDataVo;
import org.example.pojo.envcontent.TotalCommentVo;

import java.util.List;

public interface ForumChoicenessService {

    Result<List<ForumChoiceness>> queryByType(String type);

    Result<TotalCommentVo> queryComment(String postid);

    Result<Object> sendComment(SendCommentDataDto sendCommentDataDto);


    Result<Object> createContent(SubmitCreativeCenterDataDto forumChoiceness);

    Result<List<CreativeListDataVo>> ForumChoicenessList(Long userId);

    Result<Object> delateForumChoiceness(Integer id);
}
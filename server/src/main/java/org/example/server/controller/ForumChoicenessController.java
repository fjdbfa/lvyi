package org.example.server.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.common.result.Result;
import org.example.pojo.dto.CommentDTO;
import org.example.pojo.dto.GenericDto;
import org.example.pojo.dto.SendCommentDataDto;
import org.example.pojo.dto.SubmitCreativeCenterDataDto;
import org.example.pojo.entity.ForumChoiceness;
import org.example.pojo.envcontent.CreativeListDataVo;
import org.example.pojo.envcontent.TotalCommentVo;
import org.example.server.sever.ForumChoicenessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumChoicenessController {
    
    private final ForumChoicenessService forumChoicenessServiceimpl;
    
    @GetMapping("/contentData")
    public Result<List<ForumChoiceness>> choicenessData(@RequestParam String type) {
        return forumChoicenessServiceimpl.queryByType(type);
    }
    @GetMapping("/comment")
    public Result<TotalCommentVo> comment(@RequestParam String postid) {
        return forumChoicenessServiceimpl.queryComment(postid);
    }

    @PostMapping("/sendComment")
    public Result<Object> sendComment(@RequestBody SendCommentDataDto  sendCommentDataDto) {

        return  forumChoicenessServiceimpl.sendComment(sendCommentDataDto);

    }
    @PostMapping("/createContent")
    public Result<Object> createContent( @ModelAttribute SubmitCreativeCenterDataDto forumChoiceness) {
      return forumChoicenessServiceimpl.createContent(forumChoiceness);
    }
    @GetMapping("/ForumChoicenessList")
    public Result<List<CreativeListDataVo>> ForumChoicenessList(@RequestParam Long userId) {
        return forumChoicenessServiceimpl.ForumChoicenessList(userId);
    }
    @PostMapping("/delateForumChoiceness")
    public Result delateForumChoiceness(@RequestBody GenericDto data) {
        return forumChoicenessServiceimpl.delateForumChoiceness((Integer) data.getId());
    }
}
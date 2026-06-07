package org.example.server.sever.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.utils.AliyunOssUtils;
import org.example.pojo.dto.CommentDTO;
import org.example.pojo.dto.SendCommentDataDto;
import org.example.pojo.dto.SubmitCreativeCenterDataDto;
import org.example.pojo.entity.ForumChoiceness;
import org.example.pojo.envcontent.CreativeListDataVo;
import org.example.pojo.envcontent.TotalCommentVo;
import org.example.pojo.entity.PostComment;
import org.example.server.mapper.ForumChoicenessMapper;
import org.example.server.mapper.PostCommentMapper;
import org.example.server.sever.ForumChoicenessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.common.result.Result;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class ForumChoicenessServiceImpl extends ServiceImpl<ForumChoicenessMapper, ForumChoiceness> implements ForumChoicenessService {

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private AliyunOssUtils aliyunOssUtil;
    @Override
    public Result<List<ForumChoiceness>> queryByType(String type) {
        log.info("查询类型为{}的精选内容", type);
        List<ForumChoiceness> list = this.lambdaQuery()
                .eq(ForumChoiceness::getNavType, type)

                .list();
        return Result.success(list);
    }
    @Override
    public Result<TotalCommentVo> queryComment(String postid) {
        List<PostComment> postComments = postCommentMapper.selectList(
                new QueryWrapper<PostComment>()
                        .eq("post_id", postid)
                        .orderByDesc("create_time")
        );
        List<CommentDTO> commentDTOS = BeanUtil.copyToList(postComments, CommentDTO.class);
        // 这里可以添加更多的业务逻辑来构造TotalCommentVo对象
        TotalCommentVo totalCommentVo = new TotalCommentVo();
        // 设置totalCommentVo的属性
        totalCommentVo.setData(commentDTOS);
        totalCommentVo.setTotal(postComments.size());
        return Result.success(totalCommentVo);
    }

    @Override
    public Result<Object> sendComment(SendCommentDataDto sendCommentDataDto) {
        PostComment postComment = BeanUtil.copyProperties(sendCommentDataDto, PostComment.class);
        postComment.setCreateTime(LocalDateTime.now());
        postComment.setStatus(1);
        postCommentMapper.insert(postComment);
        return Result.success();
    }

    @Override
    public Result<Object> createContent(SubmitCreativeCenterDataDto forumChoiceness) {
        log.info("创建精选内容: {}", forumChoiceness);

        // 检查图片文件是否为null
        if (forumChoiceness.getImage() == null) {
            return Result.error("请上传图片");
        }
        String imgurl = aliyunOssUtil.uploadImage(forumChoiceness.getImage());

        ForumChoiceness forumChoiceness1 = BeanUtil.copyProperties(forumChoiceness, ForumChoiceness.class);
        forumChoiceness1.setImg(imgurl);
        forumChoiceness1.setCreatedAt(LocalDateTime.now());
        forumChoiceness1.setUpdatedAt(LocalDateTime.now());
        forumChoiceness1.setNumberPeople(0);
        forumChoiceness1.setNavType("最新");
        this.save(forumChoiceness1);
        return Result.success();
    }

    @Override
    public Result<List<CreativeListDataVo>> ForumChoicenessList(Long userId) {
        log.info("查询用户{}的精选内容", userId);
        List<ForumChoiceness> list = this.lambdaQuery().eq(ForumChoiceness::getUserId, userId).list();
        List<CreativeListDataVo> voList = BeanUtil.copyToList(list, CreativeListDataVo.class);
        return Result.success(voList);
    }

    @Override
    public Result delateForumChoiceness(Integer id) {
        log.info("删除精选内容: {}", id);
        this.removeById(id);
        return Result.success();
    }


}
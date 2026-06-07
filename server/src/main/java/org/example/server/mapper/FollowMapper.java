// src/main/java/com/example/demo/mapper/FollowMapper.java
package org.example.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.Follow;

import java.util.List;

@Mapper
public interface FollowMapper {

    /**
     * 查询某用户关注了谁（根据 follower_id）
     */
    List<Follow> selectByFollowerId(Long followerId);

    /**
     * 查询某用户的粉丝列表（根据 followee_id）
     */
    List<Follow> selectByFolloweeId(Long followeeId);

    /**
     * 检查是否已关注
     */
    boolean existsByFollowerAndFollowee(@Param("followerId") Long followerId,
                                        @Param("followeeId") Long followeeId);

    /**
     * 添加关注关系
     */
    void insert(Follow follow);

    /**
     * 取消关注（删除关系）
     */
    void deleteByFollowerAndFollowee(@Param("followerId") Long followerId,
                                    @Param("followeeId") Long followeeId);

    /**
     * 获取互相关注的用户（可选高级功能）
     */
    List<Follow> selectMutualFollows(Long userId);

/**
 * 根据用户ID查询其关注的用户ID列表
 *
 * @param userId 当前用户的ID
 * @return 返回当前用户关注的所有用户的ID列表，类型为Long的List集合
 */
    List<Long> selectFolloweeIdsByFollower(Long userId);

    Long countByFolloweeId(Long userId);
}
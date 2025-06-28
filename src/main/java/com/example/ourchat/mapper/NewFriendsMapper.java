package com.example.ourchat.mapper;

import com.example.ourchat.entity.FriendRequest;
import com.example.ourchat.entity.RecommendUser;
import com.example.ourchat.entity.User;
import com.example.ourchat.vo.FriendRequestVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewFriendsMapper {
    // 获取有限制的用户列表
    @Select("select * from users where user_id != #{userId} limit 5")
    List<User> getUserListLimitId(Long userId);

    // 获取朋友的id列表
    Integer getCommonFriendsCount(@Param("userId") Long userId,@Param("otherUserId") Long otherUserId);

    // 一次获取获取推荐用户
    List<RecommendUser> getRecommendFriendsWithCommonCount(Long currentUserId);

    // 获取用户请求列表
    @Select("select * from friend_requests where receiverId = #{currentUserId}")
    List<FriendRequest> getRequestListById(Long currentUserId);

    // 获取用户请求列表一次查询版本
    List<FriendRequestVO> getRequestListByIdOnce(Long receiverId);

    // 根据请求ID获取好友请求
    @Select("select * from friend_requests where request_id = #{requestId}")
    FriendRequest getFriendRequestById(Long requestId);

    // 检查是否已经是好友
    @Select("select count(*) > 0 from friends where user_id = #{userId} and friend_id = #{friendId}")
    Boolean checkFriendExists(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // 检查是否已经存在未处理的好友请求
    @Select("select count(*) > 0 from friend_requests where sender_id = #{senderId} and receiver_id = #{receiverId} and status = 0")
    Boolean existsPendingFriendRequest(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);




    // 更新好友请求状态
    @Update("update friend_requests set status = #{status}, updated_at = #{updatedAt} where request_id = #{requestId}")
    int updateFriendRequestStatus(FriendRequest friendRequest);




    // 添加好友关系
    @Insert("insert into friends (user_id, friend_id, created_at) values (#{userId}, #{friendId}, now())")
    int addFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // 添加好友请求
    int addFriendRequest(FriendRequest friendRequest);

}

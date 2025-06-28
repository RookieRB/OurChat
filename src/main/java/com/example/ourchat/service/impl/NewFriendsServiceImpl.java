package com.example.ourchat.service.impl;

import com.example.ourchat.dto.AddFriendRequestDTO;
import com.example.ourchat.dto.SearchUserDTO;
import com.example.ourchat.entity.FriendRequest;
import com.example.ourchat.entity.RecommendUser;
import com.example.ourchat.entity.User;
import com.example.ourchat.mapper.NewFriendsMapper;
import com.example.ourchat.mapper.UserMapper;
import com.example.ourchat.service.NewFriendsService;
import com.example.ourchat.service.UserService;
import com.example.ourchat.vo.FriendRequestVO;
import com.example.ourchat.vo.UserAddVO;
import io.netty.util.internal.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class NewFriendsServiceImpl implements NewFriendsService {
    @Autowired
    private NewFriendsMapper newFriendsMapper;
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;


    @Override
    public List<RecommendUser> getRecommendFriends(Long currentUserId) {
        System.out.println(currentUserId);
        List<User> users = newFriendsMapper.getUserListLimitId(currentUserId);
        System.out.println(users);
        List<RecommendUser> result = new ArrayList<>();
        // 遍历陌生人列表，获取推荐的陌生人
        for(User user : users) {
            RecommendUser recommendUser = new RecommendUser();
            recommendUser.setUserId(user.getUserId());
            recommendUser.setNickname(user.getNickname());
            recommendUser.setImgUrl(user.getImgUrl());
            recommendUser.setSameFriendsNumber(newFriendsMapper.getCommonFriendsCount(currentUserId, user.getUserId()));
            result.add(recommendUser);
        }
        return result;
    }

    @Override
    public List<RecommendUser> getRecommendFriendsOnce(Long currentUserId) {
        return newFriendsMapper.getRecommendFriendsWithCommonCount(currentUserId);
    }


    @Override
    public List<FriendRequestVO> getFriendsRequestList(Long currentUserId) {
        List<FriendRequest> friendRequestList =  newFriendsMapper.getRequestListById(currentUserId);
        List<FriendRequestVO> result = new ArrayList<>();
        for(FriendRequest friendRequest : friendRequestList) {
            FriendRequestVO friendRequestVO = new FriendRequestVO();
            BeanUtils.copyProperties(friendRequest, friendRequestVO);
            // 查找朋友的头像以及nickname
            User friendInfo = userMapper.getUserById(friendRequest.getSenderId());
            friendRequestVO.setSenderName(friendInfo.getNickname());
            friendRequestVO.setSenderAvatar(friendInfo.getImgUrl());
            result.add(friendRequestVO);
        }
        return result;
    }
    // 获取好友请求列表
    @Override
    public List<FriendRequestVO> getFriendsRequestListOnce(Long currentUserId) {
        return newFriendsMapper.getRequestListByIdOnce(currentUserId);
    }

    // 接收好友请求
    @Override
    public String acceptFriendRequest(Long requestId) {
        // 修改请求表中该请求的状态
        FriendRequest friendRequest = newFriendsMapper.getFriendRequestById(requestId);
        if (friendRequest == null) {
            return "好友请求不存在";
        }
        
        // 如果请求已经被处理过
        if (friendRequest.getStatus() != 0) {
            return "该请求已被处理";
        }
        
        // 更新请求状态为已接受(1)
        friendRequest.setStatus(1);
        friendRequest.setUpdatedAt(new Date());
        newFriendsMapper.updateFriendRequestStatus(friendRequest);

        // 判断该用户是否已经是好友
        Long senderId = friendRequest.getSenderId();
        Long receiverId = friendRequest.getReceiverId();
        
        Boolean isAlreadyFriend = newFriendsMapper.checkFriendExists(receiverId, senderId);
        if (isAlreadyFriend != null && isAlreadyFriend) {
            return "已经是好友关系";
        }
        
        // 在该用户的好友列表中添加请求的用户（双向添加好友关系）
        newFriendsMapper.addFriend(receiverId, senderId);
        newFriendsMapper.addFriend(senderId, receiverId);

        return "接收好友请求成功";
    }
    

    // 拒绝好友请求
    @Override
    public String rejectFriendRequest(Long requestId) {
        // 查询好友请求是否存在
        FriendRequest friendRequest = newFriendsMapper.getFriendRequestById(requestId);
        if (friendRequest == null) {
            return "好友请求不存在";
        }
        
        // 如果请求已经被处理过
        if (friendRequest.getStatus() != 0) {
            return "该请求已被处理";
        }
        
        // 更新请求状态为已拒绝(2)
        friendRequest.setStatus(2);
        friendRequest.setUpdatedAt(new Date());
        newFriendsMapper.updateFriendRequestStatus(friendRequest);
        
        return "已拒绝好友请求";
    }

    @Override
    public FriendRequestVO addFriendRequest(AddFriendRequestDTO addFriendRequestDTO) {
        Long receiverId = addFriendRequestDTO.getReceiverId();
        Long senderId = addFriendRequestDTO.getSenderId();
        // 是否是朋友关系？
        Boolean isFriend = newFriendsMapper.checkFriendExists(receiverId, senderId);
        if(isFriend != null && isFriend) {
           // 如果是朋友关系，那么返回null
            return null;
        }
        Boolean existsPendingFriendRequest = newFriendsMapper.existsPendingFriendRequest(senderId, receiverId);
        if(existsPendingFriendRequest != null && existsPendingFriendRequest) {
            // 已经存在未处理的好友请求
            return null;
        }

        // 创建新的好友请求
        Date currentDate = new Date();
        FriendRequest friendRequest = new FriendRequest();
        BeanUtils.copyProperties(addFriendRequestDTO, friendRequest);
        friendRequest.setStatus(0); // 设置状态为待处理
        friendRequest.setUpdatedAt(currentDate);
        friendRequest.setCreatedAt(currentDate);

        int success = newFriendsMapper.addFriendRequest(friendRequest);
        if(success == 1) {
            //如果添加成功，返回vo对象
            FriendRequestVO friendRequestVO = new FriendRequestVO();
            BeanUtils.copyProperties(friendRequest, friendRequestVO);
            User friendInfo = userMapper.getUserById(friendRequest.getSenderId());
            friendRequestVO.setSenderName(friendInfo.getNickname());
            friendRequestVO.setSenderAvatar(friendInfo.getImgUrl());
            return friendRequestVO;
        }
        return null;
    }

    @Override
    public List<UserAddVO> userSearch(SearchUserDTO searchUserDTO) {
        log.info("执行混合模糊搜索，关键词: {}", searchUserDTO.getKeyword());
        Long userId = null;
        try {
            userId = Long.parseLong(searchUserDTO.getKeyword());
        } catch (NumberFormatException e) {
            // 不是有效的数字ID，忽略异常
        }
        List<UserAddVO> result = new ArrayList<>();
        // 先查ID精确匹配
        if (userId != null) {
            User user = userMapper.getUserById(userId);
            if (user != null && !user.getUserId().equals(searchUserDTO.getCurrentUserId())) {
                Boolean isFriend = newFriendsMapper.checkFriendExists(searchUserDTO.getCurrentUserId(), user.getUserId());
                result.add(UserAddVO.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .imgUrl(user.getImgUrl())
                        .isActive(user.getIsActive())
                        .lastLogin(user.getLastLogin())
                        .phone(user.getPhone())
                        .isAlreadyFriend(isFriend != null && isFriend)
                        .build());
                return result;
            }
        }
        // 混合模糊搜索
        List<User> searchUser = userMapper.searchUsersByKeyword(
                searchUserDTO.getKeyword(),
                searchUserDTO.getCurrentUserId(),
                searchUserDTO.getLimit()
        );
        for (User user : searchUser) {
            Boolean isFriend = newFriendsMapper.checkFriendExists(searchUserDTO.getCurrentUserId(), user.getUserId());
            result.add(UserAddVO.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .imgUrl(user.getImgUrl())
                    .isActive(user.getIsActive())
                    .lastLogin(user.getLastLogin())
                    .phone(user.getPhone())
                    .isAlreadyFriend(isFriend != null && isFriend)
                    .build());
        }
        return result;
    }

}


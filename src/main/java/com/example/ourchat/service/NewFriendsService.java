package com.example.ourchat.service;


import com.example.ourchat.dto.AddFriendRequestDTO;
import com.example.ourchat.dto.SearchUserDTO;
import com.example.ourchat.entity.RecommendUser;
import com.example.ourchat.vo.FriendRequestVO;
import com.example.ourchat.vo.UserAddVO;

import java.util.List;

public interface NewFriendsService {
    List<RecommendUser> getRecommendFriends(Long currentUserId);
    List<RecommendUser> getRecommendFriendsOnce(Long currentUserId);

    List<FriendRequestVO> getFriendsRequestList(Long currentUserId);
    List<FriendRequestVO> getFriendsRequestListOnce(Long currentUserId);

    String acceptFriendRequest(Long requestId);

    String rejectFriendRequest(Long requestId);

    FriendRequestVO addFriendRequest(AddFriendRequestDTO addFriendRequestDTO);

    List<UserAddVO> userSearch(SearchUserDTO searchUserDTO);
}

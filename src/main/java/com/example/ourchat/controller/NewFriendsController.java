package com.example.ourchat.controller;

import com.example.ourchat.context.BaseContext;
import com.example.ourchat.dto.AddFriendRequestDTO;
import com.example.ourchat.dto.SearchUserDTO;
import com.example.ourchat.entity.FriendRequest;
import com.example.ourchat.entity.RecommendUser;
import com.example.ourchat.entity.User;
import com.example.ourchat.result.Result;
import com.example.ourchat.service.NewFriendsService;
import com.example.ourchat.service.UserService;
import com.example.ourchat.vo.FriendRequestVO;
import com.example.ourchat.vo.UserAddVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newFriends")
@Slf4j
public class NewFriendsController {

    @Autowired
    private NewFriendsService newFriendsService;


    // 获取推荐的好友
    @GetMapping("/recommend")
    public Result<List<RecommendUser>> getRecommendFriends() {
        System.out.println(BaseContext.getCurrentId());
        List<RecommendUser> recommendUsers = newFriendsService.getRecommendFriendsOnce(BaseContext.getCurrentId());
        return Result.success(recommendUsers);
    }
    // 获取好友请求列表
    @GetMapping("/friendsRequest")
    public Result<List<FriendRequestVO>> getFriendsRequest() {
        List<FriendRequestVO> friendRequests = newFriendsService.getFriendsRequestListOnce(BaseContext.getCurrentId());
        return Result.success(friendRequests);
    }

    @GetMapping("/acceptFriendRequest/{requestId}") 
    public Result<String> acceptFriendRequest(@PathVariable Long requestId) { 
        log.info("接收好友请求，请求ID：{}", requestId);
        
        // 参数验证
        if (requestId == null || requestId <= 0) {
            log.error("接收好友请求失败：请求ID无效，requestId={}", requestId);
            return Result.error("请求ID无效");
        }
        
        try {
            String result = newFriendsService.acceptFriendRequest(requestId);
            log.info("接收好友请求处理结果：{}", result);
            return Result.success(result); 
        } catch (Exception e) {
            log.error("接收好友请求处理异常：", e);
            return Result.error("处理好友请求时发生错误：" + e.getMessage());
        }
    } 

    @GetMapping("/rejectFriendRequest/{requestId}") 
    public Result<String> rejectFriendRequest(@PathVariable Long requestId) { 
        log.info("拒绝好友请求，请求ID：{}", requestId);
        
        // 参数验证
        if (requestId == null || requestId <= 0) {
            log.error("拒绝好友请求失败：请求ID无效，requestId={}", requestId);
            return Result.error("请求ID无效");
        }
        
        try {
            String result = newFriendsService.rejectFriendRequest(requestId);
            log.info("拒绝好友请求处理结果：{}", result);
            return Result.success(result); 
        } catch (Exception e) {
            log.error("拒绝好友请求处理异常：", e);
            return Result.error("处理好友请求时发生错误：" + e.getMessage());
        }
    } 

    @PostMapping("/addFriendRequest") 
    public Result<FriendRequestVO> addFriendRequest(@RequestBody AddFriendRequestDTO addFriendRequestDTO) { 
        log.info("添加好友请求，发送者ID：{}，接收者ID：{}", addFriendRequestDTO.getSenderId(), addFriendRequestDTO.getReceiverId());
        
        // 参数验证
        if (addFriendRequestDTO.getSenderId() == null || addFriendRequestDTO.getReceiverId() == null) {
            log.error("添加好友请求失败：发送者ID或接收者ID为空");
            return Result.error("发送者ID和接收者ID不能为空");
        }
        
        if (addFriendRequestDTO.getSenderId().equals(addFriendRequestDTO.getReceiverId())) {
            log.error("添加好友请求失败：不能添加自己为好友");
            return Result.error("不能添加自己为好友");
        }
        
        try {
            FriendRequestVO friendRequestVO = newFriendsService.addFriendRequest(addFriendRequestDTO);
            
            // 处理返回结果
            if (friendRequestVO == null) {
                // 可能是已经是好友或已经发送过请求
                log.info("添加好友请求未成功，可能已是好友或已发送过请求");
                return Result.error("添加好友请求未成功，可能已是好友或已发送过请求");
            }
            
            log.info("添加好友请求成功，请求ID：{}", friendRequestVO.getRequestId());
            return Result.success(friendRequestVO); 
        } catch (Exception e) {
            log.error("添加好友请求处理异常：", e);
            return Result.error("处理好友请求时发生错误：" + e.getMessage());
        }
    }


    @PostMapping("/search")
    public Result<List<UserAddVO>> searchUsers(@RequestBody SearchUserDTO searchUserDTO) {
        log.info("收到用户搜索请求: {}", searchUserDTO.getKeyword());

        // 参数验证
        if (searchUserDTO.getKeyword() == null || searchUserDTO.getKeyword().trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }

        // 设置当前用户ID（如果未提供）
        if (searchUserDTO.getCurrentUserId() == null) {
            searchUserDTO.setCurrentUserId(BaseContext.getCurrentId());
        }

        // 设置默认限制
        if (searchUserDTO.getLimit() == null || searchUserDTO.getLimit() <= 0) {
            searchUserDTO.setLimit(10); // 默认限制10条结果
        }

        try {
            List<UserAddVO> result = newFriendsService.userSearch(searchUserDTO);


            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索用户时发生错误", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }

}

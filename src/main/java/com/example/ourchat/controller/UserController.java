package com.example.ourchat.controller;

import com.example.ourchat.context.BaseContext;
import com.example.ourchat.entity.User;
import com.example.ourchat.result.Result;
import com.example.ourchat.service.RoomService;
import com.example.ourchat.service.UserService;
import com.example.ourchat.vo.FriendVO;
import com.example.ourchat.vo.PrivateMessageVO;
import com.example.ourchat.vo.RoomVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;

    // 获取friends列表
    @GetMapping("/friend/{userId}")
    public Result<List<FriendVO>> getFriendsList(@PathVariable("userId") Long userId){
        return Result.success(userService.getFriends(userId));
    }

    // 获取rooms列表
    @GetMapping("/room/{userId}")
    public Result<List<RoomVO>> getRoomsList(@PathVariable("userId") Long userId){
        return Result.success(roomService.getRoomsByUserId(userId));
    }
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Long id){
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @GetMapping("/getMessageWidth/{userId}")
    public Result<List<PrivateMessageVO>> getMessageWidth(@PathVariable("userId") Long userId){
        return Result.success(userService.getMessageByOtherId(userId));
    }
    @GetMapping("/isExist")
    public Result<User> isExist(){
        return Result.success(userService.getUserById(BaseContext.getCurrentId()));
    }

    @GetMapping("/getOfflineMessage/{userId}")
    public Result<List<String>> getOfflineMessage(@PathVariable("userId") Long userId){
        return Result.success(userService.getOfflineService(userId));
    }

    @PostMapping("/update")
    public Result<String> updateUser(@RequestBody User user){
        log.info(user.toString());
        return Result.success(userService.updateUser(user));
    }
}

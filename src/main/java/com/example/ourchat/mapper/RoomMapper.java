package com.example.ourchat.mapper;

import com.example.ourchat.entity.PrivateMessage;
import com.example.ourchat.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomMapper {
    // 根据房间id查看有多少人在房间内
    List<User> getUserListByRoomId(Long roomId);


    // 插入消息
    Long insertPrivateMessage(PrivateMessage privateMessage);
}

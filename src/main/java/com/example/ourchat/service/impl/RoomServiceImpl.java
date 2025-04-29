package com.example.ourchat.service.impl;

import com.example.ourchat.entity.Room;
import com.example.ourchat.entity.User;
import com.example.ourchat.mapper.RoomMapper;
import com.example.ourchat.mapper.UserMapper;
import com.example.ourchat.service.RoomService;
import com.example.ourchat.vo.RoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<RoomVO> getRoomsByUserId(Long userId) {
        List<Room> currentRooms = userMapper.getRoomsByUserId(userId);
        List<RoomVO> result = new ArrayList<>();
        for(Room room : currentRooms){

            int currentUserOnline = 0;
            // 查询房间里的用户有几个在线
            List<User> users = roomMapper.getUserListByRoomId(room.getRoomId());
            for(User user : users){
                if(user.getIsActive() != 0){
                    currentUserOnline++;
                }
            }
            // 创建一个RoomVo
            RoomVO roomVO = RoomVO.builder()
                    .roomId(room.getRoomId())
                    .roomName(room.getRoomName())
                    .imgUrl(room.getImgUrl())
                    .description(room.getDescription())
                    .onlineNumber(currentUserOnline)
                    .build();
            result.add(roomVO);
        }
        return result;
    }
}

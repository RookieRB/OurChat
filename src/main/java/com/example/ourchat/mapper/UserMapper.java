package com.example.ourchat.mapper;

import com.example.ourchat.dto.UserDTO;
import com.example.ourchat.entity.PrivateMessage;
import com.example.ourchat.entity.Room;
import com.example.ourchat.entity.User;
import com.example.ourchat.vo.FriendVO;
import com.example.ourchat.vo.RoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from users where user_id = #{id}")
    User getUserById(Long id);
    @Select("select * from users where username = #{username}")
    User getUserByUsername(String username);
    @Select("select 1 from users where username = #{username}")
    Boolean isUserExist(String username);

    int insertUser(User user);

    // 根据用户id查询用户拥有的朋友
    List<FriendVO> getFriendsById(Long userId);
    // 根据用户id查询用户所在的房间
    List<Room> getRoomsByUserId(Long userId);


    // 获取双方发送的消息
    List<PrivateMessage> getMessageFromTo(@Param("currentId") Long currentId, @Param("userId") Long userId);

}

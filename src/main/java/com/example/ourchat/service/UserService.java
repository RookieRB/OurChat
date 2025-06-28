package com.example.ourchat.service;


import com.example.ourchat.dto.SearchUserDTO;
import com.example.ourchat.dto.UserDTO;
import com.example.ourchat.entity.User;
import com.example.ourchat.vo.FriendVO;
import com.example.ourchat.vo.PrivateMessageVO;


import java.util.HashMap;
import java.util.List;

public interface UserService {
    User getUserById(Long id);

    User userLogin(UserDTO userDTO);

    Boolean isUserExist(String username);

    boolean register(User user);


    List<FriendVO> getFriends(Long userId);


    List<PrivateMessageVO> getMessageByOtherId(Long userId);

    List<String> getOfflineService(Long userId);


    String updateUser(User user);
}

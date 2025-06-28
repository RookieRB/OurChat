package com.example.ourchat.service.impl;

import com.example.ourchat.constant.MessageConstant;
import com.example.ourchat.context.BaseContext;
import com.example.ourchat.dto.SearchUserDTO;
import com.example.ourchat.dto.UserDTO;
import com.example.ourchat.entity.ChatFile;
import com.example.ourchat.entity.PrivateMessage;
import com.example.ourchat.entity.Room;
import com.example.ourchat.entity.User;
import com.example.ourchat.exception.AccountNotFoundException;
import com.example.ourchat.exception.PasswordErrorException;
import com.example.ourchat.mapper.ChatFileMapper;
import com.example.ourchat.mapper.RoomMapper;
import com.example.ourchat.mapper.UserMapper;
import com.example.ourchat.repository.ChatFileRepository;
import com.example.ourchat.service.UserService;
import com.example.ourchat.utils.RediusUtil;
import com.example.ourchat.vo.FriendVO;
import com.example.ourchat.vo.PrivateMessageVO;
import com.example.ourchat.vo.RoomVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatFileMapper chatFileMapper;
    @Autowired
    private ChatFileRepository chatFileRepository;
    @Autowired
    private RediusUtil rediusUtil;

    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public User userLogin(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        // 如果存在用户
        User isUserExit = userMapper.getUserByUsername(username);
        if(isUserExit == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if(!password.equals(isUserExit.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        return  isUserExit;
    }

    @Override
    public Boolean isUserExist(String username) {
        return userMapper.isUserExist(username);
    }

    @Override
    public boolean register(User user) {

        int result = userMapper.insertUser(user);
        if (result == 1)return true;
        log.info("用户注册",result);
        return false;
    }

    @Override
    public List<FriendVO> getFriends(Long userId) {
        log.info("获取用户的朋友列表");
        List<FriendVO> result = userMapper.getFriendsById(userId);
        return result;
    }

    @Override
    public List<PrivateMessageVO> getMessageByOtherId(Long userId) {
        Long currentId = BaseContext.getCurrentId();
        List<PrivateMessage> res = userMapper.getMessageFromTo(currentId,userId);
        List<PrivateMessageVO> resVO = new ArrayList<>();
        for(PrivateMessage message : res){
            String[] fileIdList = null;
            String[] originalNameList = null;
            String[] fileType = null;
            Long[] fileSize = null;
            if(message.isHasFile()){
                // 有文件的情况,那么我们需要遍历查询与当前文件有关的文件列表
                // 先获取当前消息所包含的文件名id
                fileIdList = chatFileMapper.getFileIdByMessageId(message.getMessageId());

                if(fileIdList != null && fileIdList.length > 0) {
                    originalNameList = new String[fileIdList.length];
                    fileType = new String[fileIdList.length];
                    fileSize = new Long[fileIdList.length];

                    // 根据 fileId查询file的信息
                    for(int i = 0; i < fileIdList.length; i++){
                        String fileId = fileIdList[i];
                        ChatFile currentFile = chatFileMapper.getChatFileByFileId(fileId);
                        originalNameList[i] = currentFile.getOriginalName();
                        fileType[i] = currentFile.getFileType();
                        fileSize[i] = currentFile.getFileSize();
                    }
                }
            } else {
                // 没有文件的情况，初始化为空数组
                fileIdList = new String[0];
                originalNameList = new String[0];
                fileType = new String[0];
                fileSize = new Long[0];
            }
            PrivateMessageVO vo = PrivateMessageVO.builder()
                    .messageId(message.getMessageId())
                    .senderId(message.getSenderId())
                    .receiverId(message.getReceiverId())
                    .content(message.getContent())
                    .hasFile(message.isHasFile())
                    .type(message.getType())
                    .sentAt(message.getSentAt())
                    .originalNameList(originalNameList)
                    .fileType(fileType)
                    .fileSize(fileSize)
                    .fileIdList(fileIdList)
                    .build();
            resVO.add(vo);
        }

        return resVO;
    }

    @Override
    public List<String> getOfflineService(Long userId) {
        log.info("获取离线信息");
        // 根据userId，从redis数据库中获取未读消息
        List<String> offLineMessage = rediusUtil.rangeString(userId.toString(), 0, -1);
        // 获取离线消息之后就将其删除
        rediusUtil.deleteListByKey(userId.toString());
        return offLineMessage;
    }

    @Override
    public String updateUser(User user) {
        try {
            Boolean result = userMapper.updateUser(user);
            if (result != null && result) {
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return "error: " + e.getMessage();
        }
    }
}

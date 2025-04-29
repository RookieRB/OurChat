package com.example.ourchat.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import com.example.ourchat.entity.*;
import com.example.ourchat.mapper.RoomMapper;
import com.example.ourchat.utils.RediusUtil;
import com.example.ourchat.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
@Slf4j
@Component
public class ChatService implements CommandLineRunner{
    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RediusUtil rediusUtil;
    // 用于视频语音通信
    // 存储 userId 到 SocketIOClient 的映射
    private final Map<Long, SocketIOClient> userSocketMap = new ConcurrentHashMap<>();
    // 在线用户，存储sessionID
    private final Set<String> onlineUsers = Collections.synchronizedSet(new HashSet<>());
    // 数据库用户 ID 到 会话 ID的映射
    private final Map<Long, String> sessionToUserIdMap = new ConcurrentHashMap<>();
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        socketIOServer.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                String sessionId = socketIOClient.getSessionId().toString();
                log.info("用户{} 加入链接成功,等待其注册", sessionId);
            }

        });

        // 注册事件：接收客户端发送的数据库用户 ID
        socketIOServer.addEventListener("register", OnlineUser.class, new DataListener<OnlineUser>() {
            @Override
            public void onData(SocketIOClient client, OnlineUser data, AckRequest ackSender) {
                String sessionId = client.getSessionId().toString();
                Long userId = data.getOnlineUserId(); // 从客户端获取数据库用户 ID
                sessionToUserIdMap.put(userId, sessionId);
                onlineUsers.add(sessionId);

                // 为语音聊天做准备
                userSocketMap.put(userId, client);
                client.set("userId", userId);

                log.info("用户{} 注册成功,它的sessionID为{}",userId,sessionId);
                log.info("当前有{}个用户", onlineUsers.size());
            }
        });


        socketIOServer.addDisconnectListener(new DisconnectListener() {

            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                String sessionId = socketIOClient.getSessionId().toString();
                onlineUsers.remove(sessionId);
                socketIOClient.getAllRooms().forEach(room -> socketIOClient.leaveRoom(room)); // 退出所有房间
                System.out.println("用户断开: " + sessionId + "，在线用户数: " + onlineUsers.size());
            }
        });


        socketIOServer.addEventListener("joinChat", JoinChatInfo.class, new DataListener<JoinChatInfo>() {

            @Override
            public void onData(SocketIOClient socketIOClient, JoinChatInfo data, AckRequest ackRequest) throws Exception {
                String room = generateRoomName(data.getCurrentUserId(), data.getCurrentChatMemberId());
                socketIOClient.joinRoom(room);
                log.info("用户{},加入{}房间",data.getCurrentUserId(), room);
                // 检查目标用户是否在线
                Long targetUserId = data.getCurrentChatMemberId();
                if (sessionToUserIdMap.containsKey(targetUserId) &&
                        onlineUsers.contains(sessionToUserIdMap.get(targetUserId))) {
                    log.info("加入房间{},对方{}在线",room,targetUserId);
                } else {
                    // 目标用户不在线，通知发起方
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("type", "userOffline");
                    notification.put("userId", targetUserId);
                    socketIOClient.sendEvent("chatNotification", notification);
                }
            }
        });
        socketIOServer.addEventListener("sendPrivateMessage",PrivateMessage.class,new DataListener<PrivateMessage>() {

            @Override
            public void onData(SocketIOClient socketIOClient, PrivateMessage data, AckRequest ackRequest) throws Exception {
                // 消息持久化到数据库中
                roomMapper.insertPrivateMessage(data);
                // 采用了自增，可以自动获取messageID
                Long messageId = data.getMessageId();
                // 获取接收方ID并验证
                Long receiverId = data.getReceiverId();
                Long senderId = data.getSenderId();
                Date sentAt = data.getSentAt();
                if(receiverId == null || senderId == null) {
                    return;
                }
                PrivateMessageVO privateMessageVO = new PrivateMessageVO();
                privateMessageVO.setMessageId(messageId);
                privateMessageVO.setReceiverId(receiverId);
                privateMessageVO.setSenderId(senderId);
                privateMessageVO.setContent(data.getContent());
                privateMessageVO.setType("message");
                privateMessageVO.setSentAt(sentAt);

                if(onlineUsers.contains(sessionToUserIdMap.get(receiverId))){
                    // 对方在线
                    String room = generateRoomName(senderId, receiverId);
                    // 需要判断对方在线的时候在不在房间里，如果在房间里，那么直接向房间里面发送，如果不在房间里，那么就直接向对方的客户端发送
                    SocketIOClient receiverClient = userSocketMap.get(receiverId);
                    if(receiverClient.getAllRooms().contains(room)){
                        // 如果对方也在房间里面
                        socketIOServer.getRoomOperations(room).sendEvent("privateMessage",privateMessageVO);
                    }else{
                        // 对方不在当前房间里面，那么直接向对方的客户端发送
                        receiverClient.sendEvent("privateMessage",privateMessageVO);
                    }

                }else{
                    // 改用Redis list存储
                    rediusUtil.rightPush(receiverId.toString(), objectMapper.writeValueAsString(privateMessageVO));

//                    Map<String,List<String>> messageStore = rediusUtil.getHashEntriesStringList(receiverId.toString());
//                    if(messageStore == null || messageStore.isEmpty()) {
//                        messageStore = new HashMap<>();
//                    }
//                    List<String> messages = messageStore.get(senderId.toString());
//                    if(messages == null) {
//                        messages = new ArrayList<>();
//                    }
//                    messages.add(objectMapper.writeValueAsString(data));
//                    messageStore.put(senderId.toString(), messages);
//
//                    rediusUtil.add(receiverId.toString(),messageStore);
//                    System.out.println("写入redis");
                }
                // 通过 ackRequest 返回 messageId 给客户端
                if(ackRequest.isAckRequested()){
                    ackRequest.sendAckData(messageId);
                }
            }
        });
        // 监听打电话
        socketIOServer.addEventListener("call", CallData.class, new DataListener<CallData>() {

            @Override
            public void onData(SocketIOClient socketIOClient, CallData callData, AckRequest ackRequest) throws Exception {
                Long targetUserId = callData.getTargetUserId();
                Long callUserId = socketIOClient.get("userId");
                SocketIOClient targetClient = userSocketMap.get(targetUserId);
                if(targetClient != null){
                    CallData data = new CallData();
                    data.setCallerId(callUserId);
                    data.setTargetUserId(targetUserId);
                    log.info("接收到用户{}的来电,发送给用户{}",callUserId,targetUserId);
                    targetClient.sendEvent("incomingCall",data);

                }else{
                    log.info("接收到用户{}的来电,但是用户{}不在线~~",callUserId,targetUserId);
                    socketIOClient.sendEvent("callFailed","用户不在线");
                }
            }
        });
        // 接受电话
        socketIOServer.addEventListener("acceptCall", AcceptCallData.class,new DataListener<AcceptCallData>() {

            @Override
            public void onData(SocketIOClient socketIOClient, AcceptCallData acceptCallData, AckRequest ackRequest) throws Exception {
                Long callerId = acceptCallData.getCallerId();
                Long accepterUserId = acceptCallData.getAccepterId();
                SocketIOClient callerClient = userSocketMap.get(callerId);
                if(callerClient != null){
                    log.info("用户{}在线，给他发送用户{}接收了电话",callerId,accepterUserId);
                    AcceptCallData data = new AcceptCallData();
                    data.setCallerId(callerId);
                    data.setAccepterId(accepterUserId);
                    callerClient.sendEvent("callAccepted",data);
                }else{
                    // 理论上来说不会到这一步,但是我们还是给发送者发送消息
                    // 因为通话请求是发起者发起的，他一般不会不在线
                    log.info("用户{}已经接收了电话，但是发起者用户{}不在线了",accepterUserId,callerId);
                    socketIOClient.sendEvent("callFailed","您接收了电话，但是对方离线了");
                }
            }
        });
        // 拒绝电话
        socketIOServer.addEventListener("rejectCall", RejectCallData.class,new DataListener<RejectCallData>() {

            @Override
            public void onData(SocketIOClient socketIOClient, RejectCallData rejectCallData, AckRequest ackRequest) throws Exception {
                Long callerId = rejectCallData.getCallerId();
                Long rejectUserId = socketIOClient.get("userId");
                SocketIOClient callerClient = userSocketMap.get(callerId);
                if(callerClient != null){
                    RejectCallData data = new RejectCallData();
                    data.setCallerId(callerId);
                    data.setRejectId(rejectUserId);
                    log.info("拨打的用户{}拒绝了本次通话",rejectUserId);
                    callerClient.sendEvent("callReject",data);
                }else{
                    System.out.println("呼叫者不在线:" + callerId);
                }
            }
        });
        // 转发 WebRTC 信令消息（如 offer、answer、ICE candidate）。
        socketIOServer.addEventListener("signaling",SignalingData.class,new DataListener<SignalingData>() {

            @Override
            public void onData(SocketIOClient socketIOClient, SignalingData signalingData, AckRequest ackRequest) throws Exception {
                Long senderId = socketIOClient.get("userId");
                Long targetUserId = signalingData.getTarget();
                SocketIOClient targetClient = userSocketMap.get(targetUserId);
                if(targetClient != null && onlineUsers.contains(sessionToUserIdMap.get(targetUserId))){
                    log.info("用户{}在线,并给他发送Type为offer的请求",targetUserId);
                    signalingData.setFrom(senderId);
                    targetClient.sendEvent("signaling",signalingData);
                }else{
                    log.info("用户{}不在线,请求截止",targetUserId);
                    System.out.println("目标用户不在线:" + targetUserId);
                }
            }
        });
        socketIOServer.start();
        System.out.println("聊天服务器启动，端口: 9092");
    }
    private String generateRoomName(Long userId1, Long userId2) {
        return userId1 < userId2 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
}


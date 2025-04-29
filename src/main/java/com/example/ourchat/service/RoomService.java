package com.example.ourchat.service;

import com.example.ourchat.vo.RoomVO;

import java.util.List;

public interface RoomService {

    List<RoomVO> getRoomsByUserId(Long userId);
}

package com.example.ourchat.controller;

import com.example.ourchat.result.Result;
import com.example.ourchat.service.UserService;
import com.example.ourchat.vo.RoomVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/room")
@Slf4j
public class RoomController {
    @Autowired
    private UserService userService;



}

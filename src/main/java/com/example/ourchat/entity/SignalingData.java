package com.example.ourchat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalingData {
    private Long from;
    private String type;
    private Long target;
    private Object sdp;
    private Object candidate;
}

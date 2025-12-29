package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    private String targetId; // ID của Post hoặc Comment
    private String targetType; // "post" hoặc "comment"
}


package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String motelId;
    private String content;
    private List<String> images = new ArrayList<>();
    private String postType = "general"; // general, announcement, question, event
}


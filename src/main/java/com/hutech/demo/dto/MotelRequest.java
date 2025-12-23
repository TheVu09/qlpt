package com.hutech.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotelRequest {
    private String landlordId;
    private String name;
    private String address;
    private String description;
    private List<String> images;
}

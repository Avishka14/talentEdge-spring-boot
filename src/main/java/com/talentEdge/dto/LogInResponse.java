package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInResponse {
    private String token;
    private boolean success;
    private String role;
    private String message;

    public LogInResponse(String message, boolean success){
        this.message = message;
        this.success = success;
    }

    public LogInResponse(String token, boolean success, String role){
        this.token = token;
        this.success = success;
        this.role = role;
    }

}

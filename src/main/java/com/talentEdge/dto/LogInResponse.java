package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogInResponse {
    private String message;
    private boolean status;
}

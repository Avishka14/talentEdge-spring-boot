package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniDTO {
    private int id;
    private int userId;
    private String degree;
    private String university;
    private LocalDate startDate;
    private LocalDate endDate;
}

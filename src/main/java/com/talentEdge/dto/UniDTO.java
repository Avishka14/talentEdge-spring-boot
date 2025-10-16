package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniDTO {
    private int id;
    private int userId;
    private String degree;
    private String university;
    private Year startDate;
    private Year endDate;
}

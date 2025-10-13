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
public class JobDTO {
    private int id;
    private String jobType;
    private String location;
    private String jobTitle;
    private String jobDescription;
    private String jobRequirements;
    private String specialization;
    private String salaryRange;
    private LocalDate postedDate;
    private LocalDate closingDate;
    private String recruiter;
    private String skills;
}

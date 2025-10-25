package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostsDTO {
    private String id;
    private String title;
    private String jobDescription;
    private String jobType;
    private String contact;
    private String salary;
    private Integer companyId;
    private Integer approvalId;

}

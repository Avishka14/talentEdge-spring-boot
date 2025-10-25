package com.talentEdge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobPosts {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    private String jobTitle;
    private String jobType;
    private String salary;
    private String contact;

    @Column(length = 2000)
    private String jobDescription;

    @ManyToOne
    @JoinColumn(name = "job_approval_id")
    private JobApproval jobApproval;


}

package com.talentEdge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "jobtypes_id")
    private JobTypes jobType;

    private String location;
    private String jobTitle;

    @Column(length = 2000)
    private String jobDescription;

    @Column(length = 2000)
    private String jobRequirements;

    @ManyToOne
    @JoinColumn(name = "specializationentity_id")
    private SpecializationEntity specialization;

    private String salaryRange;
    private LocalDate postedDate;
    private LocalDate closingDate;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;

    @ManyToOne
    @JoinColumn(name = "skills_id")
    private Skills skills;






}

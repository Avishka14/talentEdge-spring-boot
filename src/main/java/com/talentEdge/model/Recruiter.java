package com.talentEdge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String companyName;
    private String location;
    private String companyEmail;
    private String webLink;

    @Column(length = 2000)
    private String companyDescription;
}

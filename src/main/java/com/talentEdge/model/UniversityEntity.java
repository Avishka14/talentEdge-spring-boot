package com.talentEdge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Year;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;

    private String degree;
    private String university;
    private Year startDate;
    private Year endDate;


}

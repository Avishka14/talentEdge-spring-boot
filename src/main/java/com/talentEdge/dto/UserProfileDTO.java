package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String specialization;

    private String degree;
    private String university;
    private String startDate;
    private String endDate;

    private String location;

    private List<String> experienceList;
    private List<String> skills;
    private List<String> qualifications;

    private LocalDate joinedDate;
    private String about;

    private String image;


}

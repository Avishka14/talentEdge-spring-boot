package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterDTO {
    private int id;
    private String companyName;
    private String location;
    private String companyEmail;
    private String webLink;
    private String companyDescription;
}

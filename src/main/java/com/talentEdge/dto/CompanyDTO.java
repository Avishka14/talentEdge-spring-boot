package com.talentEdge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyDTO {
    private int id;
    private String name;
    private String email;
    private String webUrl;
    private String contact;
    private String about;
    private String password;

}

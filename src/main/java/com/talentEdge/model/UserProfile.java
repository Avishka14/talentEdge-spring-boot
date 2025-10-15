package com.talentEdge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private int id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "specializationentity_id")
    private SpecializationEntity specialization;


    @ElementCollection
    @CollectionTable(name = "user_experience", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "experience")
    private List<String> experienceList;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skills")
    private List<String> skills;

    @ElementCollection
    @CollectionTable(name = "user_qualifications", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "qualifications")
    private List<String> qualifications;

    private LocalDate joinedDate;

    @ElementCollection
    @CollectionTable(name = "user_location", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "location")
    private List<String> location;


}

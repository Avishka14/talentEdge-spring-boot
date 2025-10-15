package com.talentEdge.service;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.SpecializationEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.SpecializationRepository;
import com.talentEdge.repo.UserProfileRepository;
import com.talentEdge.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserServices {

    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SpecializationRepository specializationRepository;


    public UserServices(UserProfileRepository userProfileRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, SpecializationRepository specializationRepository) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.specializationRepository = specializationRepository;
    }

    public LogInResponse logIn(LogInRequest request){
        Optional<UserProfile> userProfile = userProfileRepository.findByEmail(request.getEmail());

        if(userProfile.isEmpty()){
            return new LogInResponse("User not found" , false);
        }

        UserProfile user = userProfile.get();

        if(!passwordEncoder.matches(request.getPassword() , user.getPassword())){
            return new LogInResponse("Invalid Password" , false);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LogInResponse(token, true);

    }

    public String register(UserProfile user) {
        if (userProfileRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }

        if (user.getSpecialization() == null) {
            throw new RuntimeException("Specialization is required");
        }

        SpecializationEntity specialization = specializationRepository
                .findById(user.getSpecialization().getId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        user.setSpecialization(specialization);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setJoinedDate(LocalDate.now());

        userProfileRepository.save(user);

        return "User registered successfully";
    }


}



package com.talentEdge.service;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.dto.UserProfileDTO;
import com.talentEdge.model.SpecializationEntity;
import com.talentEdge.model.UniversityEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.SpecializationRepository;
import com.talentEdge.repo.UniversityRepository;
import com.talentEdge.repo.UserProfileRepository;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServices {

    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SpecializationRepository specializationRepository;
    private final UniversityRepository universityRepository;

    public UserServices(UserProfileRepository userProfileRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, SpecializationRepository specializationRepository, UniversityRepository universityRepository) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.specializationRepository = specializationRepository;
        this.universityRepository = universityRepository;
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

    public String register(UserProfile user , HttpServletResponse response) {
        if (userProfileRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }

        if (user.getSpecialization() == null) {
            throw new RuntimeException("Specialization is required");
        }

        Random random = new Random();
        int number;
        do {
            number = 1000 + random.nextInt(9000);
        } while (userProfileRepository.existsById(number));


        user.setId(number);

        SpecializationEntity specialization = specializationRepository
                .findById(user.getSpecialization().getId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        user.setSpecialization(specialization);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setJoinedDate(LocalDate.now());

        userProfileRepository.save(user);

        ResponseCookie cookie = ResponseCookie.from("user" , String.valueOf(number))
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie" , cookie.toString());

        return "User registered successfully" ;
    }

    public UserProfileDTO fetchUserInfoById(Integer id){

        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User id is invalid"));

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setSpecialization(user.getSpecialization().getValue());
        dto.setEmail(user.getEmail());
        dto.setJoinedDate(user.getJoinedDate());
        dto.setSkills(user.getSkills());
        return dto;

    }


}



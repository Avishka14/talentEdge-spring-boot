package com.talentEdge.service;

import com.talentEdge.dto.*;
import com.talentEdge.model.*;
import com.talentEdge.repo.*;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServices {

    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SpecializationRepository specializationRepository;
    private final UniversityRepository universityRepository;
    private final ProfilephotoRepository profilephotoRepository;
    private final RoleRepository roleRepository;

    private static final String UPLOAD_DIR = "uploads/profile_photos/";

    public UserServices(UserProfileRepository userProfileRepository, BCryptPasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil, SpecializationRepository specializationRepository,
                        UniversityRepository universityRepository, ProfilephotoRepository profilephotoRepository,
                        RoleRepository roleRepository) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.specializationRepository = specializationRepository;
        this.universityRepository = universityRepository;
        this.profilephotoRepository = profilephotoRepository;
        this.roleRepository = roleRepository;
    }


    public LogInResponse logIn(LogInRequest request, HttpServletResponse response) {
        log.info("Login attempt for email {}", request.getEmail());

        Optional<UserProfile> userProfile = userProfileRepository.findByEmail(request.getEmail());
        if (userProfile.isEmpty()) {
            log.warn("Login failed — User not found for email {}", request.getEmail());
            return new LogInResponse("User not found", false);
        }

        UserProfile user = userProfile.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed — Invalid password for email {}", request.getEmail());
            return new LogInResponse("Invalid Password", false);
        }

        ResponseCookie cookie = ResponseCookie.from("user", String.valueOf(user.getId()))
                .httpOnly(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Login successful for user {}", request.getEmail());

        return new LogInResponse(token, true, user.getRole().getRole());
    }


    public Response register(UserProfile user, HttpServletResponse response) {
        log.info("Register attempt for email {}", user.getEmail());

        if (userProfileRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Registration failed — Email {} already exists", user.getEmail());
            return new Response("Email already exists", false);
        }

        if (user.getSpecialization() == null) {
            log.error("Registration failed — Specialization missing for email {}", user.getEmail());
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

        Role userRole = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        user.setRole(userRole);

        userProfileRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        ResponseCookie cookie = ResponseCookie.from("user", String.valueOf(number))
                .httpOnly(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return new Response("Success", true);
    }


    public UserProfileDTO fetchUserInfoById(Integer id) {
        log.info("Fetching user info for ID {}", id);

        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User id is invalid"));

        String photoUrl = profilephotoRepository.findFirstByUserProfile_Id(id)
                .map(ProfilePhoto::getPhotoUrl)
                .orElse(null);

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setSpecialization(user.getSpecialization().getValue());
        dto.setEmail(user.getEmail());
        dto.setJoinedDate(user.getJoinedDate());
        dto.setSkills(user.getSkills());
        dto.setLocation(user.getLocation());
        dto.setAbout(user.getAbout());
        dto.setImage(photoUrl);

        log.info("Fetched user info for ID {}", id);
        return dto;
    }


    public UniDTO fetchUniById(Integer id) {
        log.info("Fetching university info for user ID {}", id);

        UniversityEntity data = universityRepository.findFirstByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("University Not Found"));

        UniDTO uni = new UniDTO();
        uni.setDegree(data.getDegree());
        uni.setUniversity(data.getUniversity());
        uni.setStartDate(data.getStartDate());
        uni.setEndDate(data.getEndDate());

        log.info("Fetched university info for user ID {}", id);
        return uni;
    }


    public SkillsDTO fetchSkilsById(Integer id) {
        log.info("Fetching skills for user ID {}", id);

        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        SkillsDTO dto = new SkillsDTO();
        dto.setValue(user.getSkills());

        log.info("Fetched skills for user ID {}", id);
        return dto;
    }

    public SkillsDTO saveSkills(SkillsDTO dto) {
        log.info("Saving skills for user ID {}", dto.getUserId());

        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentSkills = Optional.ofNullable(user.getSkills()).orElse(new ArrayList<>());
        currentSkills.addAll(dto.getValue());
        user.setSkills(currentSkills.stream().distinct().collect(Collectors.toList()));

        userProfileRepository.save(user);
        log.info("Skills saved for user ID {}", dto.getUserId());

        return dto;
    }

    public SkillsDTO deleteSkillsById(SkillsDTO dto) {
        log.info("Deleting skills for user ID {}", dto.getUserId());

        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentSkills = Optional.ofNullable(user.getSkills()).orElse(new ArrayList<>());
        currentSkills.removeAll(dto.getValue());
        user.setSkills(currentSkills);

        userProfileRepository.save(user);
        log.info("Skills deleted for user ID {}", dto.getUserId());

        return dto;
    }


    public ProfeQualificationsDTO fetchQualifById(Integer id) {
        log.info("Fetching qualifications for user ID {}", id);

        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        ProfeQualificationsDTO dto = new ProfeQualificationsDTO();
        dto.setQualifications(user.getQualifications());

        log.info("Fetched qualifications for user ID {}", id);
        return dto;
    }

    public ProfeQualificationsDTO saveQualifficatio(ProfeQualificationsDTO dto) {
        log.info("Saving qualifications for user ID {}", dto.getUserId());

        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentQualifications = Optional.ofNullable(user.getQualifications()).orElse(new ArrayList<>());
        currentQualifications.addAll(dto.getQualifications());
        user.setQualifications(currentQualifications.stream().distinct().collect(Collectors.toList()));

        userProfileRepository.save(user);
        log.info("Qualifications saved for user ID {}", dto.getUserId());

        return dto;
    }

    public ProfeQualificationsDTO deleteQualification(ProfeQualificationsDTO dto) {
        log.info("Deleting qualifications for user ID {}", dto.getUserId());

        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentQualifi = Optional.ofNullable(user.getQualifications()).orElse(new ArrayList<>());
        currentQualifi.removeAll(dto.getQualifications());
        user.setQualifications(currentQualifi);

        userProfileRepository.save(user);
        log.info("Qualifications deleted for user ID {}", dto.getUserId());

        return dto;
    }


    public String updateUser(UserProfile userProfile) {
        log.info("Updating user ID {}", userProfile.getId());

        UserProfile existingUser = userProfileRepository.findById(userProfile.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID"));

        if (userProfile.getFirstName() != null) existingUser.setFirstName(userProfile.getFirstName());
        if (userProfile.getLastName() != null) existingUser.setLastName(userProfile.getLastName());
        if (userProfile.getEmail() != null) existingUser.setEmail(userProfile.getEmail());
        if (userProfile.getSpecialization() != null) existingUser.setSpecialization(userProfile.getSpecialization());
        if (userProfile.getLocation() != null) existingUser.setLocation(userProfile.getLocation());
        if (userProfile.getAbout() != null) existingUser.setAbout(userProfile.getAbout());

        userProfileRepository.save(existingUser);
        log.info("User updated successfully ID {}", userProfile.getId());

        return "User Update Successfully";
    }


    public UserProfilePhotoDTO uploadAndSaveProfilePhoto(MultipartFile file, Integer userId) {
        log.info("Uploading profile photo for user ID {}", userId);

        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
            File directory = new File(uploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                log.error("Failed to create upload directory {}", uploadDir);
                throw new IOException("Failed to create upload directory");
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + File.separator + uniqueFileName;
            file.transferTo(new File(filePath));

            String imgUrl = "/" + UPLOAD_DIR + uniqueFileName;

            ProfilePhoto profilePhoto = profilephotoRepository.findFirstByUserProfile_Id(userId)
                    .orElseGet(() -> {
                        ProfilePhoto newPhoto = new ProfilePhoto();
                        UserProfile userProfile = userProfileRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("User not found"));
                        newPhoto.setUserProfile(userProfile);
                        return newPhoto;
                    });

            profilePhoto.setPhotoUrl(imgUrl);
            profilephotoRepository.save(profilePhoto);

            log.info("Profile photo uploaded successfully for user ID {}", userId);
            return UserProfilePhotoDTO.builder()
                    .userId(userId)
                    .imgUrl(imgUrl)
                    .build();

        } catch (IOException e) {
            log.error("Error uploading profile photo for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error saving profile photo", e);
        }
    }

}

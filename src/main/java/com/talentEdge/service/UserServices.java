package com.talentEdge.service;

import com.talentEdge.dto.*;
import com.talentEdge.model.*;
import com.talentEdge.repo.*;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;

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

    public UserServices(UserProfileRepository userProfileRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, SpecializationRepository specializationRepository, UniversityRepository universityRepository, ProfilephotoRepository profilephotoRepository, RoleRepository roleRepository) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.specializationRepository = specializationRepository;
        this.universityRepository = universityRepository;
        this.profilephotoRepository = profilephotoRepository;
        this.roleRepository = roleRepository;
    }

    public LogInResponse logIn(LogInRequest request, HttpServletResponse response) {
        Optional<UserProfile> userProfile = userProfileRepository.findByEmail(request.getEmail());

        if (userProfile.isEmpty()) {
            return new LogInResponse("User not found", false);
        }

        UserProfile user = userProfile.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LogInResponse("Invalid Password", false);
        }

        ResponseCookie cookie = ResponseCookie.from("user", String.valueOf(user.getId()))
                .httpOnly(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        String token = jwtUtil.generateToken(user.getEmail());


        String role = user.getRole().getRole();

        return new LogInResponse(token, true, role);
    }


    public Response register(UserProfile user , HttpServletResponse response) {
        if (userProfileRepository.findByEmail(user.getEmail()).isPresent()) {
            return new Response("Email already exists" , false);
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

        Role userRole = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        user.setRole(userRole);

        userProfileRepository.save(user);

        ResponseCookie cookie = ResponseCookie.from("user" , String.valueOf(number))
                .httpOnly(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie" , cookie.toString());

        return new Response("Success" , true);
    }

    public UserProfileDTO fetchUserInfoById(Integer id){

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
        return dto;

    }

    public UniDTO fetchUniById(Integer id){

        UniversityEntity data = universityRepository.findFirstByUserId(id)
                .orElseThrow( () -> new NoSuchElementException("University Not Found"));

        UniDTO uni = new UniDTO();
        uni.setDegree(data.getDegree());
        uni.setUniversity(data.getUniversity());
        uni.setStartDate(data.getStartDate());
        uni.setEndDate(data.getEndDate());
        return uni;
    }

    public SkillsDTO fetchSkilsById(Integer id){

        UserProfile user = userProfileRepository.findById(id)
        .orElseThrow( () -> new NoSuchElementException("User Not Found"));

        SkillsDTO dto = new SkillsDTO();
        dto.setValue(user.getSkills());
        return dto;

    }

    public SkillsDTO saveSkills(SkillsDTO dto) {
        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentSkills = user.getSkills();
        if (currentSkills == null) {
            currentSkills = new ArrayList<>();
        }

        currentSkills.addAll(dto.getValue());

        currentSkills = currentSkills.stream().distinct().collect(Collectors.toList());

        user.setSkills(currentSkills);
        userProfileRepository.save(user);

        return dto;
    }


    public SkillsDTO deleteSkillsById(SkillsDTO dto) {
        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentSkills = user.getSkills();
        if (currentSkills == null) {
            currentSkills = new ArrayList<>();
        }

        currentSkills.removeAll(dto.getValue());

        user.setSkills(currentSkills);
        userProfileRepository.save(user);

        return dto;
    }


    public ProfeQualificationsDTO fetchQualifById(Integer id){

        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

         ProfeQualificationsDTO dto = new ProfeQualificationsDTO();
         dto.setQualifications(user.getQualifications());
         return dto;

    }

    public ProfeQualificationsDTO saveQualifficatio(ProfeQualificationsDTO dto){

        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentQualifications = user.getQualifications();
        if (currentQualifications == null) {
            currentQualifications = new ArrayList<>();
        }

        currentQualifications.addAll(dto.getQualifications());

        currentQualifications = currentQualifications.stream().distinct().collect(Collectors.toList());
        user.setSkills(currentQualifications);
        userProfileRepository.save(user);

        return dto;
    }

    public ProfeQualificationsDTO deleteQualification(ProfeQualificationsDTO dto) {
        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not Found"));

        List<String> currentQualifi = user.getQualifications();
        if (currentQualifi == null) {
            currentQualifi = new ArrayList<>();
        }

        currentQualifi.removeAll(dto.getQualifications());

        user.setQualifications(currentQualifi);
        userProfileRepository.save(user);

        return dto;
    }

    public String updateUser(UserProfile userProfile){

        UserProfile existingUser = userProfileRepository.findById(userProfile.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID" ));

        if (userProfile.getFirstName() != null) existingUser.setFirstName(userProfile.getFirstName());
        if (userProfile.getLastName() != null) existingUser.setLastName(userProfile.getLastName());
        if (userProfile.getEmail() != null) existingUser.setEmail(userProfile.getEmail());
        if (userProfile.getSpecialization() != null) existingUser.setSpecialization(userProfile.getSpecialization());
        if (userProfile.getLocation() != null) existingUser.setLocation(userProfile.getLocation());
        if (userProfile.getAbout() != null) existingUser.setAbout(userProfile.getAbout());
        userProfileRepository.save(existingUser);

        return "User Update Successfully";

    }


    public UserProfilePhotoDTO uploadAndSaveProfilePhoto(MultipartFile file, Integer userId) {
        try {

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "profile_photos";

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) throw new IOException("Failed to create upload directory");
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + File.separator + uniqueFileName;

            file.transferTo(new File(filePath));

            String imgUrl = "/uploads/profile_photos/" + uniqueFileName;

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

            return UserProfilePhotoDTO.builder()
                    .userId(userId)
                    .imgUrl(imgUrl)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Error saving profile photo", e);
        }
    }








}



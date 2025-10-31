package com.talentEdge.service;

import com.talentEdge.dto.*;
import com.talentEdge.model.SpecializationEntity;
import com.talentEdge.model.UniversityEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.*;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServicesTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserServices userServices;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {

        LogInRequest request = new LogInRequest();
        request.setEmail("Avishka@example.com");
        request.setPassword("password");

        UserProfile user = new UserProfile();
        user.setId(1);
        user.setEmail("Avishka@example.com");
        user.setPassword("encodedPassword");

        when(userProfileRepository.findByEmail("Avishka@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("Avishka@example.com")).thenReturn("mockToken");


        Response result = userServices.logIn(request, response);

        assertTrue(result.isStatus());
        assertEquals("mockToken", result.getMessage());
        verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testLogin_UserNotFound() {
        LogInRequest request = new LogInRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password");

        when(userProfileRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Response result = userServices.logIn(request, response);

        assertFalse(result.isStatus());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        LogInRequest request = new LogInRequest();
        request.setEmail("Avishka@example.com");
        request.setPassword("wrong");

        UserProfile user = new UserProfile();
        user.setPassword("encodedPassword");

        when(userProfileRepository.findByEmail("Avishka@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        Response result = userServices.logIn(request, response);

        assertFalse(result.isStatus());
        assertEquals("Invalid Password", result.getMessage());
    }

    @Test
    void testRegister_Success() {
        UserProfile newUser = new UserProfile();
        newUser.setEmail("new@example.com");
        newUser.setPassword("plain");
        newUser.setSpecialization(new SpecializationEntity(1, "IT"));

        when(userProfileRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(specializationRepository.findById(1)).thenReturn(Optional.of(newUser.getSpecialization()));
        when(passwordEncoder.encode("plain")).thenReturn("encodedPassword");

        Response responseObj = userServices.register(newUser, response);

        assertTrue(responseObj.isStatus());
        assertEquals("Success", responseObj.getMessage());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        UserProfile existing = new UserProfile();
        existing.setEmail("existing@example.com");

        when(userProfileRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existing));

        Response responseObj = userServices.register(existing, response);

        assertFalse(responseObj.isStatus());
        assertEquals("Email already exists", responseObj.getMessage());
    }

    @Test
    void testRegister_SpecializationMissing() {
        UserProfile user = new UserProfile();
        user.setEmail("user@example.com");
        user.setPassword("pass");

        when(userProfileRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userServices.register(user, response));
    }


    @Test
    void testFetchUserInfoById_Success() {
        UserProfile user = new UserProfile();
        user.setId(1);
        user.setFirstName("Avishka");
        user.setLastName("Chamod");
        user.setEmail("avishka@example.com");
        user.setJoinedDate(LocalDate.of(2024, 1, 10));
        user.setLocation("Colombo");
        user.setAbout("Java Developer");

        SpecializationEntity specialization = new SpecializationEntity();
        specialization.setValue("Software Engineering");
        user.setSpecialization(specialization);

        when(userProfileRepository.findById(1)).thenReturn(Optional.of(user));

        UserProfileDTO result = userServices.fetchUserInfoById(1);

        assertNotNull(result);
        assertEquals("Avishka", result.getFirstName());
        assertEquals("Chamod", result.getLastName());
        assertEquals("Software Engineering", result.getSpecialization());
        assertEquals("avishka@example.com", result.getEmail());
        verify(userProfileRepository, times(1)).findById(1);
    }

    @Test
    void testFetchUserInfoById_UserNotFound() {

        when(userProfileRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServices.fetchUserInfoById(99);
        });

        assertEquals("User id is invalid", exception.getMessage());
        verify(userProfileRepository, times(1)).findById(99);
    }

    @Test
    void testFetchUniById_Success() {

        UniversityEntity entity = new UniversityEntity();
        entity.setDegree("BSc Computer Science");
        entity.setUniversity("University of Colombo");
        entity.setStartDate(Year.of(2022));
        entity.setEndDate(Year.of(2026));

        when(universityRepository.findFirstByUserId(1)).thenReturn(Optional.of(entity));

        UniDTO result = userServices.fetchUniById(1);

        assertNotNull(result);
        assertEquals("BSc Computer Science", result.getDegree());
        assertEquals("University of Colombo", result.getUniversity());
        assertEquals(Year.of(2022), result.getStartDate());
        assertEquals(Year.of(2026), result.getEndDate());
        verify(universityRepository, times(1)).findFirstByUserId(1);
    }

    @Test
    void testFetchUniById_UniversityNotFound() {
        when(universityRepository.findFirstByUserId(99)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userServices.fetchUniById(99);
        });

        assertEquals("University Not Found", exception.getMessage());
        verify(universityRepository, times(1)).findFirstByUserId(99);
    }

    @Test
    void testFetchSkilsById_Success() {

        Integer userId = 1;

        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setSkills(List.of("Java", "Spring Boot", "MySQL"));

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(user));


        SkillsDTO result = userServices.fetchSkilsById(userId);

        assertNotNull(result);
        assertEquals(List.of("Java", "Spring Boot", "MySQL"), result.getValue());

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    void testFetchSkilsById_UserNotFound() {

        Integer userId = 99;
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userServices.fetchSkilsById(userId));

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    void testSaveSkills_Success() {

        Integer userId = 1;

        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setSkills(new ArrayList<>(List.of("Java")));

        SkillsDTO dto = new SkillsDTO();
        dto.setUserId(userId);
        dto.setValue(List.of("Spring Boot", "MySQL"));


        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));


        SkillsDTO result = userServices.saveSkills(dto);

        assertNotNull(result);
        assertEquals(dto.getValue(), result.getValue()); // returned same DTO
        assertEquals(List.of("Java", "Spring Boot", "MySQL"), user.getSkills());

        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, times(1)).save(user);
    }

    @Test
    void testSaveSkills_UserNotFound() {

        Integer userId = 10;

        SkillsDTO dto = new SkillsDTO();
        dto.setUserId(userId);
        dto.setValue(List.of("React", "Node.js"));

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userServices.saveSkills(dto));

        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testDeleteSkillsById_Success() {

        Integer userId = 1;

        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setSkills(new ArrayList<>(List.of("Java", "Spring Boot", "MySQL")));

        SkillsDTO dto = new SkillsDTO();
        dto.setUserId(userId);
        dto.setValue(List.of("Spring Boot"));

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        SkillsDTO result = userServices.deleteSkillsById(dto);


        assertNotNull(result);
        assertEquals(List.of("Java", "MySQL"), user.getSkills());

        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, times(1)).save(user);
    }

    @Test
    void testDeleteSkillsById_UserNotFound() {

        Integer userId = 10;
        SkillsDTO dto = new SkillsDTO();
        dto.setUserId(userId);
        dto.setValue(List.of("React"));

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userServices.deleteSkillsById(dto));

        verify(userProfileRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testFetchQualifById_Success() {

        Integer userId = 1;

        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setQualifications(List.of("AWS Certified", "Java Developer"));

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(user));

        ProfeQualificationsDTO result = userServices.fetchQualifById(userId);

        assertNotNull(result);
        assertEquals(List.of("AWS Certified", "Java Developer"), result.getQualifications());

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    void testFetchQualifById_UserNotFound() {

        Integer userId = 99;
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userServices.fetchQualifById(userId));

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    void testSaveQualifficatio_Success() {
        // Arrange
        ProfeQualificationsDTO dto = new ProfeQualificationsDTO();
        dto.setUserId(1);
        dto.setQualifications(List.of("AWS Certified", "Spring Boot"));

        UserProfile user = new UserProfile();
        user.setId(1);
        user.setQualifications(new ArrayList<>(List.of("Java Developer")));

        when(userProfileRepository.findById(1)).thenReturn(Optional.of(user));

        ProfeQualificationsDTO result = userServices.saveQualifficatio(dto);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertTrue(result.getQualifications().contains("AWS Certified"));
        assertTrue(result.getQualifications().contains("Spring Boot"));

        verify(userProfileRepository, times(1)).findById(1);
        verify(userProfileRepository, times(1)).save(user);
    }

    @Test
    void testSaveQualifficatio_UserNotFound() {
        ProfeQualificationsDTO dto = new ProfeQualificationsDTO();
        dto.setUserId(99);
        dto.setQualifications(List.of("Docker Expert"));

        when(userProfileRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userServices.saveQualifficatio(dto));

        verify(userProfileRepository, times(1)).findById(99);
    }

    @Test
    void testDeleteQualification_Success() {

        ProfeQualificationsDTO dto = new ProfeQualificationsDTO();
        dto.setUserId(1);
        dto.setQualifications(List.of("AWS Certified"));

        UserProfile user = new UserProfile();
        user.setId(1);
        user.setQualifications(new ArrayList<>(List.of("AWS Certified", "Java Developer", "Spring Boot")));

        when(userProfileRepository.findById(1)).thenReturn(Optional.of(user));

        ProfeQualificationsDTO result = userServices.deleteQualification(dto);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertFalse(user.getQualifications().contains("AWS Certified")); // should be removed
        assertTrue(user.getQualifications().contains("Java Developer"));
        assertTrue(user.getQualifications().contains("Spring Boot"));

        verify(userProfileRepository, times(1)).findById(1);
        verify(userProfileRepository, times(1)).save(user);
    }

    @Test
    void testDeleteQualification_UserNotFound() {

        ProfeQualificationsDTO dto = new ProfeQualificationsDTO();
        dto.setUserId(99);
        dto.setQualifications(List.of("Python Certified"));

        when(userProfileRepository.findById(99)).thenReturn(Optional.empty());


        assertThrows(NoSuchElementException.class, () -> userServices.deleteQualification(dto));

        verify(userProfileRepository, times(1)).findById(99);
    }

    @Test
    void testUpdateUser_Success() {

        UserProfile existingUser = new UserProfile();
        existingUser.setId(1);
        existingUser.setFirstName("Avishka");
        existingUser.setLastName("Chamod");
        existingUser.setEmail("avishka@example.com");
        existingUser.setLocation("Colombo");
        existingUser.setAbout("Experienced Java developer");

        UserProfile updatedUser = new UserProfile();
        updatedUser.setId(1);
        updatedUser.setFirstName("Avishka");
        updatedUser.setLocation("Kandy");

        when(userProfileRepository.findById(1)).thenReturn(Optional.of(existingUser));


        String result = userServices.updateUser(updatedUser);

        assertEquals("User Update Successfully", result);
        assertEquals("Avishka", existingUser.getFirstName());
        assertEquals("Chamod", existingUser.getLastName());
        assertEquals("avishka@example.com", existingUser.getEmail());
        assertEquals("Kandy", existingUser.getLocation());
        assertEquals("Experienced Java developer", existingUser.getAbout());

        verify(userProfileRepository, times(1)).findById(1);
        verify(userProfileRepository, times(1)).save(existingUser);
    }


    @Test
    void testUpdateUser_UserNotFound() {
        UserProfile newUser = new UserProfile();
        newUser.setId(99);
        newUser.setFirstName("Test");

        when(userProfileRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userServices.updateUser(newUser));

        verify(userProfileRepository, times(1)).findById(99);
        verify(userProfileRepository, never()).save(any());
    }




















}

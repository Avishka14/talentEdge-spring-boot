package com.talentEdge.service;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.SpecializationEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.*;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServicesTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private SpecializationRepository specializationRepository;

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


        LogInResponse result = userServices.logIn(request, response);

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

        LogInResponse result = userServices.logIn(request, response);

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

        LogInResponse result = userServices.logIn(request, response);

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

        LogInResponse responseObj = userServices.register(newUser, response);

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

        LogInResponse responseObj = userServices.register(existing, response);

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
}

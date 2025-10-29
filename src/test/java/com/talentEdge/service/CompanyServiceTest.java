package com.talentEdge.service;

import com.talentEdge.dto.*;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CompanyServices companyServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCompanyLogin_Success() {
        LogInRequest request = new LogInRequest();
        request.setEmail("test@company.com");
        request.setPassword("password123");

        CompanyEntity company = new CompanyEntity();
        company.setId(1);
        company.setEmail("test@company.com");
        company.setPassword("encodedPassword");

        when(companyRepository.findByEmail("test@company.com")).thenReturn(Optional.of(company));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@company.com")).thenReturn("fake-jwt-token");

        LogInResponse result = companyServices.companyLogIn(request, response);

        assertTrue(result.isStatus());
        assertEquals("fake-jwt-token", result.getMessage());

        verify(companyRepository, times(1)).findByEmail("test@company.com");
        verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testCompanyLogin_InvalidPassword() {
        LogInRequest request = new LogInRequest();
        request.setEmail("test@company.com");
        request.setPassword("password123");

        CompanyEntity company = new CompanyEntity();
        company.setEmail("test@company.com");
        company.setPassword("encodedPassword");

        when(companyRepository.findByEmail("test@company.com")).thenReturn(Optional.of(company));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        LogInResponse result = companyServices.companyLogIn(request, response);

        assertFalse(result.isStatus());
        assertEquals("Invalid Password", result.getMessage());
    }

    @Test
    void testCompanyRegister_Success() {
        CompanyEntity company = new CompanyEntity();
        company.setEmail("test@company.com");
        company.setPassword("plainPassword");
        company.setAbout("A good company");
        company.setContact("1234567890");
        company.setWebUrl("www.company.com");

        when(companyRepository.findByEmail("test@company.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPass");
        when(companyRepository.existsById(anyInt())).thenReturn(false);

        RegistrationResponse result = companyServices.companyRegister(company, response);

        assertTrue(result.isStatus());
        assertEquals("Company Successfully Saved", result.getMessage());
        verify(companyRepository, times(1)).save(company);
    }

    @Test
    void testCompanyRegister_EmailAlreadyUsed() {
        CompanyEntity company = new CompanyEntity();
        company.setEmail("exists@company.com");

        when(companyRepository.findByEmail("exists@company.com"))
                .thenReturn(Optional.of(new CompanyEntity()));

        RegistrationResponse result = companyServices.companyRegister(company, response);

        assertFalse(result.isStatus());
        assertEquals("Company E-Mail Already used", result.getMessage());
    }

    @Test
    void testFetchCompanyDataById_Success() {
        CompanyEntity company = new CompanyEntity();
        company.setId(1);
        company.setName("TalentEdge");
        company.setEmail("contact@talentedge.com");
        company.setContact("12345");
        company.setWebUrl("www.talentedge.com");
        company.setAbout("Tech Company");

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        CompanyDTO dto = companyServices.fetchCompanyDataByID(1);

        assertEquals("TalentEdge", dto.getName());
        assertEquals("contact@talentedge.com", dto.getEmail());
        assertEquals("Tech Company", dto.getAbout());
    }

    @Test
    void testUpdateCompanyData_Success() {
        CompanyEntity existing = new CompanyEntity();
        existing.setId(1);
        existing.setName("OldName");
        existing.setAbout("Old About");

        CompanyDTO update = new CompanyDTO();
        update.setId(1);
        update.setName("NewName");
        update.setAbout("New About");

        when(companyRepository.findById(1)).thenReturn(Optional.of(existing));

        boolean result = companyServices.updateCompanyData(update);

        assertTrue(result);
        assertEquals("NewName", existing.getName());
        assertEquals("New About", existing.getAbout());
        verify(companyRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateCompanyData_NotFound() {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(99);

        when(companyRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> companyServices.updateCompanyData(dto));
    }










}

package com.talentEdge.service;

import com.talentEdge.dto.UniDTO;
import com.talentEdge.model.University;
import com.talentEdge.model.UniversityEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.UniversityRepository;
import com.talentEdge.repo.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniversityServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UniversityService universityService;

    private UserProfile mockUser;
    private UniDTO mockUniDTO;
    private UniversityEntity mockEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new UserProfile();
        mockUser.setId(1);

        mockUniDTO = new UniDTO();
        mockUniDTO.setUserId(1);
        mockUniDTO.setUniversity("MIT");
        mockUniDTO.setDegree("BSc Computer Science");
        mockUniDTO.setStartDate(Year.of(2022));
        mockUniDTO.setEndDate(Year.of(2026));

        mockEntity = UniversityEntity.builder()
                .id(1)
                .user(mockUser)
                .university("MIT")
                .degree("BSc Computer Science")
                .startDate(Year.of(2022))
                .endDate(Year.of(2026))
                .build();
    }


    @Test
    void testGetUniversitiesByCountry_Success() {
        String country = "United States";

        University u1 = new University();
        u1.setName("MIT");

        University u2 = new University();
        u2.setName("Harvard");

        List<University> universities = List.of(u1, u2);
        ResponseEntity<List<University>> responseEntity = new ResponseEntity<>(universities, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<University>>>any(),
                anyMap()
        )).thenReturn(responseEntity);

        List<University> result = universityService.getUniversitiesByCountry(country);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("MIT", result.get(0).getName());
    }



    @Test
    void testGetUniversitiesByCountry_Failure() {
        String country = "UnknownLand";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<University>>>any(),
                anyMap()
        )).thenThrow(new RuntimeException("API failure"));

        List<University> result = universityService.getUniversitiesByCountry(country);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testSaveUniversity_Success() {
        when(userProfileRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(universityRepository.save(any(UniversityEntity.class))).thenReturn(mockEntity);

        UniversityEntity result = universityService.saveUniversity(mockUniDTO);

        assertNotNull(result);
        assertEquals("MIT", result.getUniversity());
        verify(universityRepository, times(1)).save(any(UniversityEntity.class));
    }


    @Test
    void testSaveUniversity_UserNotFound() {
        when(userProfileRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> universityService.saveUniversity(mockUniDTO));

        assertEquals("User not found with id", exception.getMessage());
    }


    @Test
    void testUpdateUniversity_Success() {
        when(universityRepository.findFirstByUserId(1)).thenReturn(Optional.of(mockEntity));
        when(universityRepository.save(any(UniversityEntity.class))).thenReturn(mockEntity);

        UniversityEntity result = universityService.updateUniversity(mockUniDTO);

        assertNotNull(result);
        assertEquals("MIT", result.getUniversity());
        verify(universityRepository, times(1)).save(any(UniversityEntity.class));
    }

    @Test
    void testUpdateUniversity_NotFound() {
        when(universityRepository.findFirstByUserId(1)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> universityService.updateUniversity(mockUniDTO));

        assertEquals("User Not found", exception.getMessage());
    }
}

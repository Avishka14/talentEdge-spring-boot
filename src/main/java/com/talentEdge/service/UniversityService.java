package com.talentEdge.service;

import com.talentEdge.dto.UniDTO;
import com.talentEdge.model.University;
import com.talentEdge.model.UniversityEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.UniversityRepository;
import com.talentEdge.repo.UserProfileRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class UniversityService {

    private final RestTemplate restTemplate;
    private final UniversityRepository universityRepository;
    private final UserProfileRepository userProfileRepository;

    private static final String BASE_URL = "http://universities.hipolabs.com";

    public UniversityService(RestTemplate restTemplate, UniversityRepository universityRepository, UserProfileRepository userProfileRepository) {
        this.restTemplate = restTemplate;
        this.universityRepository = universityRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public List<University> getUniversitiesByCountry(String countryName) {

        String urlTemplate = BASE_URL + "/search?country={country}";
        Map<String, String> params = Collections.singletonMap("country", countryName);

        try {
            ResponseEntity<List<University>> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<University>>() {},
                    params
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                System.err.println("API call returned non-successful status or null body for country: " + countryName);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("Error fetching universities for country: " + countryName + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public UniversityEntity saveUniversity(UniDTO dto) {
        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id"));

        UniversityEntity university = UniversityEntity.builder()
                .user(user)
                .degree(dto.getDegree())
                .university(dto.getUniversity())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        return universityRepository.save(university);
    }

    public UniversityEntity updateUniversity(UniDTO dto){

        UniversityEntity uni = universityRepository.findFirstByUserId(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not found"));

        uni.setUniversity(dto.getUniversity());
        uni.setDegree(dto.getDegree());
        uni.setStartDate(dto.getStartDate());
        uni.setEndDate(dto.getEndDate());

        return universityRepository.save(uni);

    }




}

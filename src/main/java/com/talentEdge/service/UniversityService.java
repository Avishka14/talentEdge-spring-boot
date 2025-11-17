package com.talentEdge.service;

import com.talentEdge.dto.UniDTO;
import com.talentEdge.model.University;
import com.talentEdge.model.UniversityEntity;
import com.talentEdge.model.UserProfile;
import com.talentEdge.repo.UniversityRepository;
import com.talentEdge.repo.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class UniversityService {

    private final RestTemplate restTemplate;
    private final UniversityRepository universityRepository;
    private final UserProfileRepository userProfileRepository;

    private static final String BASE_URL = "http://universities.hipolabs.com";

    public UniversityService(RestTemplate restTemplate,
                             UniversityRepository universityRepository,
                             UserProfileRepository userProfileRepository) {
        this.restTemplate = restTemplate;
        this.universityRepository = universityRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public List<University> getUniversitiesByCountry(String countryName) {

        String urlTemplate = BASE_URL + "/search?country={country}";
        Map<String, String> params = Collections.singletonMap("country", countryName);

        try {
            log.info("Fetching universities for country: {}", countryName);

            ResponseEntity<List<University>> response = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<University>>() {},
                    params
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Received {} universities for {}",
                        response.getBody().size(), countryName);
                return response.getBody();
            } else {
                log.warn("API returned empty response for {}", countryName);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Failed to fetch universities for {} - {}", countryName, e.getMessage());
            return Collections.emptyList();
        }
    }

    public UniversityEntity saveUniversity(UniDTO dto) {

        log.info("Saving university details for userId {}", dto.getUserId());

        UserProfile user = userProfileRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id"));

        UniversityEntity university = UniversityEntity.builder()
                .user(user)
                .degree(dto.getDegree())
                .university(dto.getUniversity())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        log.info("University saved successfully for user {}", user.getId());

        return universityRepository.save(university);
    }

    public UniversityEntity updateUniversity(UniDTO dto) {

        log.info("Updating university details for userId {}", dto.getUserId());

        UniversityEntity uni = universityRepository.findFirstByUserId(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User Not found"));

        uni.setUniversity(dto.getUniversity());
        uni.setDegree(dto.getDegree());
        uni.setStartDate(dto.getStartDate());
        uni.setEndDate(dto.getEndDate());

        log.info("University updated successfully for user {}", dto.getUserId());

        return universityRepository.save(uni);
    }
}

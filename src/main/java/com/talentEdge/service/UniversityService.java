package com.talentEdge.service;

import com.talentEdge.model.University;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UniversityService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://universities.hipolabs.com";

    public UniversityService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
}

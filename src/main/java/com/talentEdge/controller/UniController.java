package com.talentEdge.controller;

import com.talentEdge.model.University;
import com.talentEdge.service.UniversityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin("*")
public class UniController {

    private final UniversityService universityService;

    public UniController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping()
    public List<University> getUniversities(@RequestParam String country) {
        return universityService.getUniversitiesByCountry(country);
    }

}

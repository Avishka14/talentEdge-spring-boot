package com.talentEdge.controller;

import com.talentEdge.dto.UniDTO;
import com.talentEdge.model.University;
import com.talentEdge.model.UniversityEntity;
import com.talentEdge.service.UniversityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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

    @PostMapping("/save")
    public ResponseEntity<UniversityEntity> createUniversity(@RequestBody UniDTO dto){
        try {
            UniversityEntity uni = universityService.saveUniversity(dto);
            return ResponseEntity.ok(uni);

        } catch (NoSuchElementException e) {
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("/update")
    public ResponseEntity<UniversityEntity> updateUniversity(@RequestBody UniDTO dto){
        try {
            UniversityEntity uni = universityService.updateUniversity(dto);
            return ResponseEntity.ok(uni);

        } catch (NoSuchElementException e) {
                return ResponseEntity.noContent().build();
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().build();
        }
    }


}

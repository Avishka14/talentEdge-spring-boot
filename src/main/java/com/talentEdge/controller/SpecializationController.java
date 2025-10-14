package com.talentEdge.controller;

import com.talentEdge.model.SpecializationEntity;
import com.talentEdge.service.SpecializationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/specializations")
@CrossOrigin("*")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @GetMapping("/getall")
    public List<SpecializationEntity> getAllSpecializations(){
        return specializationService.getAllSpecializations();
    }

}

package com.talentEdge.service;

import com.talentEdge.model.SpecializationEntity;
import com.talentEdge.repo.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public List<SpecializationEntity> getAllSpecializations(){
        return specializationRepository.findAll();
    }

}

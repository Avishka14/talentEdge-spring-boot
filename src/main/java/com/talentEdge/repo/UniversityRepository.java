package com.talentEdge.repo;

import com.talentEdge.model.UniversityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityRepository extends JpaRepository<UniversityEntity , Integer> {
    Optional<UniversityEntity> findFirstByUserId(Integer userId);
}

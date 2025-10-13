package com.talentEdge.repo;

import com.talentEdge.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<UserProfile , Integer> {
}

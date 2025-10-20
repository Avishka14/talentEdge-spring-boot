package com.talentEdge.repo;

import com.talentEdge.model.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity , Integer> {

    Optional<CompanyEntity> findByEmail(String email);

}

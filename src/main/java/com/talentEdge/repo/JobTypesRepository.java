package com.talentEdge.repo;

import com.talentEdge.model.JobTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTypesRepository extends JpaRepository<JobTypes , Integer> {
}

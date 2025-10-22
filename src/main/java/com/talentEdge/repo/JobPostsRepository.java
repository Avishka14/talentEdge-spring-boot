package com.talentEdge.repo;

import com.talentEdge.model.JobPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostsRepository extends JpaRepository<JobPosts , Integer> {
}

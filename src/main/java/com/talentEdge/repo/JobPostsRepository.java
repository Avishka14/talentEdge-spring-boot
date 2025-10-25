package com.talentEdge.repo;

import com.talentEdge.model.JobPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostsRepository extends JpaRepository<JobPosts , String> {
    List<JobPosts> findByCompany_Id(Integer companyId);
}

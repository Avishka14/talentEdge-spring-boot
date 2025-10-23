package com.talentEdge.controller;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.JobPosts;
import com.talentEdge.service.JobPostsServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobposts")
@CrossOrigin("*")
public class JobPostsController {

    private final JobPostsServices jobPostsServices;

    public JobPostsController(JobPostsServices jobPostsServices) {
        this.jobPostsServices = jobPostsServices;
    }

    @PostMapping("/open")
    public ResponseEntity<LogInResponse> openNewJobPost(@RequestBody JobPostsDTO jobPosts){
        LogInResponse response = jobPostsServices.openNewJoPost(jobPosts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch/{companyId}")
    public List<JobPostsDTO> getJobsByCompany(@PathVariable Integer companyId) {
        return jobPostsServices.fetchJobOpeningsByID(companyId);
    }


}

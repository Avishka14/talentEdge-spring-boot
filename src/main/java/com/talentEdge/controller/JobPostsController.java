package com.talentEdge.controller;

import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.JobPosts;
import com.talentEdge.service.JobPostsServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobposts")
@CrossOrigin("*")
public class JobPostsController {

    private final JobPostsServices jobPostsServices;

    public JobPostsController(JobPostsServices jobPostsServices) {
        this.jobPostsServices = jobPostsServices;
    }

    @PostMapping("/open")
    public ResponseEntity<LogInResponse> openNewJobPost(@RequestBody JobPosts jobPosts){

        LogInResponse response = jobPostsServices.openNewJoPost(jobPosts);
        return ResponseEntity.ok(response);

    }

}

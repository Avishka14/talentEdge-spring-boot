package com.talentEdge.controller;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.JobPosts;
import com.talentEdge.service.JobPostsServices;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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

    @PostMapping("/update")
    public ResponseEntity<LogInResponse> updateExistingJobPost(@RequestBody JobPostsDTO jobPostsDTO){
        try {

            LogInResponse response = jobPostsServices.updateJobPost(jobPostsDTO);
            return ResponseEntity.ok(response);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<LogInResponse> removeJobPostbyId(@PathVariable String id){

        try {

            LogInResponse logInResponse = jobPostsServices.removeJobPosting(id);
            return ResponseEntity.ok(logInResponse);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }

    }


}

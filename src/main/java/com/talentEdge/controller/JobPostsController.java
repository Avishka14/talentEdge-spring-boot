package com.talentEdge.controller;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.Response;
import com.talentEdge.service.JobPostsServices;
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
    public ResponseEntity<Response> openNewJobPost(@RequestBody JobPostsDTO jobPosts){
        Response response = jobPostsServices.openNewJoPost(jobPosts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch/{companyId}")
    public ResponseEntity<List<JobPostsDTO>> getJobsByCompany(@PathVariable Integer companyId) {
        try{
            return ResponseEntity.ok(jobPostsServices.fetchJobOpeningsByID(companyId));

        }catch (NoSuchElementException E){
             return  ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Response> updateExistingJobPost(@RequestBody JobPostsDTO jobPostsDTO){
        try {

            Response response = jobPostsServices.updateJobPost(jobPostsDTO);
            return ResponseEntity.ok(response);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Response> removeJobPostbyId(@PathVariable String id){

        try {

            Response response = jobPostsServices.removeJobPosting(id);
            return ResponseEntity.ok(response);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping("/fetchall")
    public ResponseEntity<List<JobPostsDTO>> fetchAllJobPosts() {

        List<JobPostsDTO> jobPosts = jobPostsServices.fetchAllJoPosts();

        return ResponseEntity.ok(jobPosts);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Response> approveJobPost(@PathVariable String id){
        try {

            Response response = jobPostsServices.approveJobPosting(id);
            return ResponseEntity.ok(response);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<Response> declineJobPost(@PathVariable String id){
        try {

            Response response = jobPostsServices.declineJobPost(id);
            return ResponseEntity.ok(response);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }




}

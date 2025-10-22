package com.talentEdge.service;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.JobApproval;
import com.talentEdge.model.JobPosts;
import com.talentEdge.repo.JobPostsRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
public class JobPostsServices {

    private final JobPostsRepository jobPostsRepository;

    public JobPostsServices(JobPostsRepository jobPostsRepository) {
        this.jobPostsRepository = jobPostsRepository;
    }

    public LogInResponse openNewJoPost(JobPosts jobPosts){

        LogInResponse response;

        if(jobPosts.getJobType().isEmpty()){
            return new LogInResponse("JobType not provided" , false);
        }else if(jobPosts.getJobTitle().isEmpty()){
            return new LogInResponse("Job Title" , false);
        }else if(jobPosts.getJobDescription().isEmpty()){
            return new LogInResponse("Job Description is not Provided" , false);
        }else if(jobPosts.getContact().isEmpty()){
            return new LogInResponse("Contact E-Mail is not Provided" , false);
        }else if(jobPosts.getSalary().isEmpty()){
            return new LogInResponse("Salary is not Provided" , false);
        }else{
            JobApproval waiting = new JobApproval();
            waiting.setId(1);

            jobPosts.setJobApproval(waiting);

            jobPostsRepository.save(jobPosts);

             response = new LogInResponse("Successfilly Opened Job Post Please wait for Approval" , true);

        }

        return response;

    }

}

package com.talentEdge.service;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.JobApproval;
import com.talentEdge.model.JobPosts;
import com.talentEdge.repo.JobPostsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    public List<JobPostsDTO> fetchJobOpeningsByID(Integer companyId) {
        List<JobPosts> jobPostsList = jobPostsRepository.findByCompany_Id(companyId);

        if (jobPostsList.isEmpty()) {
            throw new NoSuchElementException("No job postings found for company ID: " + companyId);
        }

        return jobPostsList.stream()
                .map(post -> JobPostsDTO.builder()
                        .id(post.getId())
                        .title(post.getJobTitle())
                        .jobDescription(post.getJobDescription())
                        .jobType(post.getJobType())
                        .contact(post.getContact())
                        .salary(post.getSalary())
                        .companyId(post.getCompany().getId())
                        .approvalId(post.getJobApproval().getId())
                        .build()
                )
                .collect(Collectors.toList());
    }

}

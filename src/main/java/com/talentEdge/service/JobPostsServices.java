package com.talentEdge.service;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.model.JobApproval;
import com.talentEdge.model.JobPosts;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.repo.JobPostsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class JobPostsServices {

    private final JobPostsRepository jobPostsRepository;
    private final CompanyRepository companyRepository;

    public JobPostsServices(JobPostsRepository jobPostsRepository, CompanyRepository companyRepository) {
        this.jobPostsRepository = jobPostsRepository;
        this.companyRepository = companyRepository;
    }

    public LogInResponse openNewJoPost(JobPostsDTO jobPosts){

        LogInResponse response;

        if(jobPosts.getJobType().isEmpty()){
            return new LogInResponse("JobType not provided" , false);
        }else if(jobPosts.getTitle().isEmpty()){
            return new LogInResponse("Job Title" , false);
        }else if(jobPosts.getJobDescription().isEmpty()){
            return new LogInResponse("Job Description is not Provided" , false);
        }else if(jobPosts.getContact().isEmpty()){
            return new LogInResponse("Contact E-Mail is not Provided" , false);
        }else if(jobPosts.getSalary().isEmpty()){
            return new LogInResponse("Salary is not Provided" , false);
        }else{

           JobPosts model = new JobPosts();
           CompanyEntity company = companyRepository.findById(jobPosts.getCompanyId())
                   .orElseThrow(() -> new NoSuchElementException("Company Not Found"));

           JobApproval approval = new JobApproval();
           approval.setId(1);
           if(company != null){

               model.setCompany(company);
               model.setJobTitle(jobPosts.getTitle());
               model.setSalary(jobPosts.getSalary());
               model.setJobType(jobPosts.getJobType());
               model.setContact(jobPosts.getContact());
               model.setJobApproval(approval);
               model.setJobDescription(jobPosts.getJobDescription());

               jobPostsRepository.save(model);
               response = new LogInResponse("Successfilly Opened Job Post Please wait for Approval" , true);

           }else{
               response = new LogInResponse("Error in Company Profile" , false);
           }


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

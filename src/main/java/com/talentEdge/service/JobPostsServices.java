package com.talentEdge.service;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.Response;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.model.JobApproval;
import com.talentEdge.model.JobPosts;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.repo.JobPostsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobPostsServices {

    private final JobPostsRepository jobPostsRepository;
    private final CompanyRepository companyRepository;

    public JobPostsServices(JobPostsRepository jobPostsRepository, CompanyRepository companyRepository) {
        this.jobPostsRepository = jobPostsRepository;
        this.companyRepository = companyRepository;
    }


    public Response openNewJoPost(JobPostsDTO jobPosts) {

        log.info("Attempt to create new job post for company ID {}", jobPosts.getCompanyId());

        if (jobPosts.getJobType().isEmpty()) {
            return new Response("JobType not provided", false);
        } else if (jobPosts.getTitle().isEmpty()) {
            return new Response("Job Title", false);
        } else if (jobPosts.getJobDescription().isEmpty()) {
            return new Response("Job Description is not Provided", false);
        } else if (jobPosts.getContact().isEmpty()) {
            return new Response("Contact E-Mail is not Provided", false);
        } else if (jobPosts.getSalary().isEmpty()) {
            return new Response("Salary is not Provided", false);
        } else {

            CompanyEntity company = companyRepository.findById(jobPosts.getCompanyId())
                    .orElseThrow(() -> new NoSuchElementException("Company Not Found"));

            Random random = new Random();
            int number;
            String id;
            do {
                number = 1000 + random.nextInt(9000);
                id = "JBPST: " + number;
            } while (jobPostsRepository.existsById(id));

            JobApproval approval = new JobApproval();
            approval.setId(1);

            JobPosts model = new JobPosts();
            model.setId(id);
            model.setCompany(company);
            model.setJobTitle(jobPosts.getTitle());
            model.setSalary(jobPosts.getSalary());
            model.setJobType(jobPosts.getJobType());
            model.setContact(jobPosts.getContact());
            model.setJobApproval(approval);
            model.setJobDescription(jobPosts.getJobDescription());

            jobPostsRepository.save(model);

            log.info("New job post {} created for company {}", id, company.getEmail());

            return new Response("Successfilly Opened Job Post Please wait for Approval", true);
        }
    }


    public List<JobPostsDTO> fetchJobOpeningsByID(Integer companyId) {

        log.info("Fetching job posts for company ID {}", companyId);

        List<JobPosts> jobPostsList = jobPostsRepository.findByCompany_Id(companyId);

        if (jobPostsList.isEmpty()) {
            log.warn("No job posts found for company ID {}", companyId);
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
                        .build())
                .collect(Collectors.toList());
    }


    public Response updateJobPost(JobPostsDTO jobPostsDTO) {

        log.info("Updating job post {}", jobPostsDTO.getId());

        JobPosts jobPosts = jobPostsRepository.findById(jobPostsDTO.getId())
                .orElseThrow(() -> {
                    log.error("Job post {} not found", jobPostsDTO.getId());
                    return new NoSuchElementException("Job post not found");
                });

        JobApproval approval = new JobApproval();
        approval.setId(1);

        if (jobPostsDTO.getTitle() != null) jobPosts.setJobTitle(jobPostsDTO.getTitle());
        if (jobPostsDTO.getJobType() != null) jobPosts.setJobType(jobPostsDTO.getJobType());
        if (jobPostsDTO.getJobDescription() != null) jobPosts.setJobDescription(jobPostsDTO.getJobDescription());
        if (jobPostsDTO.getSalary() != null) jobPosts.setSalary(jobPostsDTO.getSalary());
        if (jobPostsDTO.getContact() != null) jobPosts.setContact(jobPostsDTO.getContact());

        jobPosts.setJobApproval(approval);

        jobPostsRepository.save(jobPosts);

        log.info("Job post {} updated successfully", jobPostsDTO.getId());

        return new Response("Job Post Successfully Updated", true);
    }


    public Response removeJobPosting(String jobPostingId) {

        log.info("Attempt to remove job post {}", jobPostingId);

        JobPosts post = jobPostsRepository.findById(jobPostingId)
                .orElseThrow(() -> {
                    log.error("Job post {} not found for removal", jobPostingId);
                    return new NoSuchElementException("Job Post not Found");
                });

        jobPostsRepository.delete(post);

        log.info("Job post {} removed successfully", jobPostingId);

        return new Response("Success", true);
    }

    public List<JobPostsDTO> fetchAllJoPosts() {

        log.info("Fetching all unapproved job posts");

        List<JobPosts> jobPostsList = jobPostsRepository.findAll();

        List<JobPosts> notApprovedPosts = jobPostsList.stream()
                .filter(post -> post.getJobApproval() == null || post.getJobApproval().getId() == 1)
                .toList();

        if (notApprovedPosts.isEmpty()) {
            log.warn("No unapproved job posts found");
            throw new NoSuchElementException("No unapproved job posts found");
        }

        return notApprovedPosts.stream()
                .map(post -> JobPostsDTO.builder()
                        .id(post.getId())
                        .title(post.getJobTitle())
                        .jobDescription(post.getJobDescription())
                        .jobType(post.getJobType())
                        .contact(post.getContact())
                        .salary(post.getSalary())
                        .companyId(post.getCompany().getId())
                        .approvalId(post.getJobApproval() == null ? null : post.getJobApproval().getId())
                        .build()
                ).collect(Collectors.toList());
    }


    public Response approveJobPosting(String id) {

        log.info("Approving job post {}", id);

        JobPosts jobPosts = jobPostsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Job post {} not found for approval", id);
                    return new NoSuchElementException("Job Posting not found");
                });

        JobApproval approval = new JobApproval();
        approval.setId(2);

        jobPosts.setJobApproval(approval);
        jobPostsRepository.save(jobPosts);

        log.info("Job post {} approved", id);

        return new Response("Approval Success", true);
    }


    public Response declineJobPost(String id) {

        log.info("Declining job post {}", id);

        JobPosts jobPosts = jobPostsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Job post {} not found for decline", id);
                    return new NoSuchElementException("Job Posting not found");
                });

        JobApproval approval = new JobApproval();
        approval.setId(3);

        jobPosts.setJobApproval(approval);
        jobPostsRepository.save(jobPosts);

        log.info("Job post {} declined", id);

        return new Response("Decline Success", true);
    }
}

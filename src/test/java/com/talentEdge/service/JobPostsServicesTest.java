package com.talentEdge.service;

import com.talentEdge.dto.JobPostsDTO;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.model.JobApproval;
import com.talentEdge.model.JobPosts;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.repo.JobPostsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobPostsServicesTest {

    @Mock
    private JobPostsRepository jobPostsRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private JobPostsServices jobPostsServices;

    public JobPostsServicesTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOpenNewJobPost_Success() {
        CompanyEntity company = new CompanyEntity();
        company.setId(1);

        JobPostsDTO dto = new JobPostsDTO();
        dto.setCompanyId(1);
        dto.setTitle("Java Developer");
        dto.setJobDescription("Full stack developer");
        dto.setJobType("Remote");
        dto.setSalary("100000");
        dto.setContact("hr@company.com");

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(jobPostsRepository.existsById(anyString())).thenReturn(false);

        LogInResponse response = jobPostsServices.openNewJoPost(dto);

        assertTrue(response.isStatus());
        assertEquals("Successfilly Opened Job Post Please wait for Approval", response.getMessage());
        verify(jobPostsRepository, times(1)).save(any(JobPosts.class));
    }

    @Test
    void testOpenNewJobPost_MissingFields() {
        JobPostsDTO dto = new JobPostsDTO();
        dto.setJobType("");
        LogInResponse response = jobPostsServices.openNewJoPost(dto);

        assertFalse(response.isStatus());
        assertEquals("JobType not provided", response.getMessage());
    }

    @Test
    void testFetchJobOpeningsByID_Success() {
        CompanyEntity company = new CompanyEntity();
        company.setId(1);

        JobApproval approval = new JobApproval();
        approval.setId(1);

        JobPosts post = new JobPosts();
        post.setId("JBPST: 1234");
        post.setJobTitle("Software Engineer");
        post.setJobDescription("Backend work");
        post.setJobType("Onsite");
        post.setContact("contact@company.com");
        post.setSalary("2000 USD");
        post.setCompany(company);
        post.setJobApproval(approval);

        when(jobPostsRepository.findByCompany_Id(1)).thenReturn(List.of(post));

        var result = jobPostsServices.fetchJobOpeningsByID(1);

        assertEquals(1, result.size());
        assertEquals("Software Engineer", result.get(0).getTitle());
    }

    @Test
    void testFetchJobOpeningsByID_EmptyListThrowsException() {
        when(jobPostsRepository.findByCompany_Id(99)).thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () -> jobPostsServices.fetchJobOpeningsByID(99));
    }


    @Test
    void testUpdateJobPost_Success() {
        JobPosts post = new JobPosts();
        post.setId("JBPST: 1234");
        post.setJobTitle("Old Title");

        JobPostsDTO dto = new JobPostsDTO();
        dto.setId("JBPST: 1234");
        dto.setTitle("New Title");

        when(jobPostsRepository.findById("JBPST: 1234")).thenReturn(Optional.of(post));

        LogInResponse response = jobPostsServices.updateJobPost(dto);

        assertTrue(response.isStatus());
        assertEquals("Job Post Successfully Updated", response.getMessage());
        assertEquals("New Title", post.getJobTitle());
    }

    @Test
    void testRemoveJobPosting_Success() {
        JobPosts post = new JobPosts();
        post.setId("JBPST: 5555");

        when(jobPostsRepository.findById("JBPST: 5555")).thenReturn(Optional.of(post));

        LogInResponse response = jobPostsServices.removeJobPosting("JBPST: 5555");

        assertTrue(response.isStatus());
        verify(jobPostsRepository, times(1)).delete(post);
    }

    @Test
    void testRemoveJobPosting_NotFound() {
        when(jobPostsRepository.findById("JBPST: 9999")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> jobPostsServices.removeJobPosting("JBPST: 9999"));
    }
}

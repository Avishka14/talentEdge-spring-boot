package com.talentEdge.service;

import com.talentEdge.dto.CompanyDTO;
import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.Response;
import com.talentEdge.dto.RegistrationResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class CompanyServices {

    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public CompanyServices(CompanyRepository companyRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Response companyLogIn(LogInRequest request , HttpServletResponse response){

        Optional<CompanyEntity> company = companyRepository.findByEmail(request.getEmail());
        if(company.isEmpty()){
            log.warn("Login failed: Company not found for {}", request.getEmail());
            return new Response("Company Not found" , false);

        }

        CompanyEntity companyEntity = company.get();

        if(!passwordEncoder.matches(request.getPassword() , companyEntity.getPassword())){
            log.warn("Login failed: Invalid password attempt for {}", request.getEmail());
            return new Response("Invalid Password" , false);
        }

        ResponseCookie cookie = ResponseCookie.from("company" , String.valueOf(companyEntity.getId()))
                .httpOnly(false)
                .path("/")
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie" , cookie.toString());
        log.debug("Login cookie issued for company ID {}", companyEntity.getId());

        String token = jwtUtil.generateToken(companyEntity.getEmail());
        log.info("Login successful for company {}", companyEntity.getEmail());
        return new Response(token , true);

    }

    public RegistrationResponse companyRegister(CompanyEntity company, HttpServletResponse response) {

        log.info("Company registration attempt for email {}", company.getEmail());

        if (companyRepository.findByEmail(company.getEmail()).isPresent()) {
            log.warn("Registration failed: Email already used {}", company.getEmail());
            return new RegistrationResponse("Company E-Mail Already used", false);
        }

        if (company.getAbout().isEmpty()) {
            log.warn("Registration failed: About section empty for {}", company.getEmail());
            return new RegistrationResponse("Please Enter the About", false);
        }
        if (company.getContact().isEmpty()) {
            log.warn("Registration failed: Contact empty for {}", company.getEmail());
            return new RegistrationResponse("Please Enter the Contact", false);
        }
        if (company.getWebUrl().isEmpty()) {
            log.warn("Registration failed: Web URL empty for {}", company.getEmail());
            return new RegistrationResponse("Please Enter the Web Url", false);
        }

        Random random = new Random();
        int number;
        do {
            number = 100 + random.nextInt(900);
        } while (companyRepository.existsById(number));

        log.debug("Generated unique ID {} for new company {}", number, company.getEmail());

        company.setId(number);
        company.setPassword(passwordEncoder.encode(company.getPassword()));

        companyRepository.save(company);
        log.info("Company successfully registered with ID {}", number);

        ResponseCookie cookie = ResponseCookie.from("company", String.valueOf(number))
                .httpOnly(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        log.debug("Registration cookie issued for company ID {}", number);

        return new RegistrationResponse("Company Successfully Saved", true);
    }

    public CompanyDTO fetchCompanyDataByID(Integer companyId) {

        log.info("Fetching company data for ID {}", companyId);

        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> {
                    log.info("Company not found for ID {}", companyId);
                    return new NoSuchElementException("Company Not Found");
                });

        log.debug("Fetched company details for ID {}", companyId);

        CompanyDTO dto = new CompanyDTO();
        dto.setName(company.getName());
        dto.setContact(company.getContact());
        dto.setEmail(company.getEmail());
        dto.setWebUrl(company.getWebUrl());
        dto.setAbout(company.getAbout());

        return dto;
    }


    public boolean updateCompanyData(CompanyDTO companyDTO) {

        log.info("Updating company data for ID {}", companyDTO.getId());

        CompanyEntity company = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> {
                    log.error("Company update failed: ID {} not found", companyDTO.getId());
                    return new NoSuchElementException("Company Not Found");
                });

        if (companyDTO.getName() != null) company.setName(companyDTO.getName());
        if (companyDTO.getPassword() != null) company.setPassword(companyDTO.getPassword());
        if (companyDTO.getAbout() != null) company.setAbout(companyDTO.getAbout());
        if (companyDTO.getWebUrl() != null) company.setWebUrl(companyDTO.getWebUrl());
        if (companyDTO.getContact() != null) company.setContact(companyDTO.getContact());

        companyRepository.save(company);
        log.info("Company data updated successfully for ID {}", companyDTO.getId());

        return true;
    }


}

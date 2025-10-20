package com.talentEdge.service;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CompanyServices {

    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public CompanyServices(CompanyRepository companyRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LogInResponse companyLogIn(LogInRequest request , HttpServletResponse response){

        Optional<CompanyEntity> company = companyRepository.findByEmail(request.getEmail());
        if(company.isEmpty()){
            return new LogInResponse("Company Not found" , false);
        }

        CompanyEntity companyEntity = company.get();

        if(!passwordEncoder.matches(request.getPassword() , companyEntity.getPassword())){
            return new LogInResponse("Invalid Password" , false);
        }

        ResponseCookie cookie = ResponseCookie.from("company" , String.valueOf(companyEntity.getId()))
                .httpOnly(false)
                .path("/")
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie" , cookie.toString());

        String token = jwtUtil.generateToken(companyEntity.getEmail());
        return new LogInResponse(token , true);

    }

}

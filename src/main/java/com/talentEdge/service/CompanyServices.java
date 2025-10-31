package com.talentEdge.service;

import com.talentEdge.dto.CompanyDTO;
import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.Response;
import com.talentEdge.dto.RegistrationResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.repo.CompanyRepository;
import com.talentEdge.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

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

    public Response companyLogIn(LogInRequest request , HttpServletResponse response){

        Optional<CompanyEntity> company = companyRepository.findByEmail(request.getEmail());
        if(company.isEmpty()){
            return new Response("Company Not found" , false);
        }

        CompanyEntity companyEntity = company.get();

        if(!passwordEncoder.matches(request.getPassword() , companyEntity.getPassword())){
            return new Response("Invalid Password" , false);
        }

        ResponseCookie cookie = ResponseCookie.from("company" , String.valueOf(companyEntity.getId()))
                .httpOnly(false)
                .path("/")
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie" , cookie.toString());

        String token = jwtUtil.generateToken(companyEntity.getEmail());
        return new Response(token , true);

    }

    public RegistrationResponse companyRegister(CompanyEntity company , HttpServletResponse response){

        if(companyRepository.findByEmail(company.getEmail()).isPresent()){
            return new RegistrationResponse("Company E-Mail Already used" , false);
        }

        if(company.getAbout().isEmpty()){
            return new RegistrationResponse("Please Enter the About" , false);
        }else if(company.getContact().isEmpty()){
            return new RegistrationResponse("Please Enter the Contact" , false);
        }else if(company.getWebUrl().isEmpty()){
            return new RegistrationResponse("Please Enter the Web Url" , false);
        }else{

            Random random = new Random();
            int number;
            do{
                number = 100+ random.nextInt(900);
            }while(companyRepository.existsById(number));

            company.setId(number);
            company.setPassword(passwordEncoder.encode(company.getPassword()));

            companyRepository.save(company);

            ResponseCookie cookie = ResponseCookie.from("company" , String.valueOf(number))
                    .httpOnly(false)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();
            response.addHeader("Set-Cookie" , cookie.toString());

            return new RegistrationResponse("Company Successfully Saved" , true);
        }
    }

    public CompanyDTO fetchCompanyDataByID(Integer companyId){

        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Company Not Found"));
        CompanyDTO dto = new CompanyDTO();
        dto.setName(company.getName());
        dto.setContact(company.getContact());
        dto.setEmail(company.getEmail());
        dto.setWebUrl(company.getWebUrl());
        dto.setAbout(company.getAbout());
        return dto;

    }

    public boolean updateCompanyData(CompanyDTO companyDTO){
        CompanyEntity company = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("Company Not Found"));


        if (companyDTO.getName() != null) company.setName(companyDTO.getName());
        if (companyDTO.getPassword() != null) company.setPassword(companyDTO.getPassword());
        if (companyDTO.getAbout() != null) company.setAbout(companyDTO.getAbout());
        if (companyDTO.getWebUrl() != null) company.setWebUrl(companyDTO.getWebUrl());
        if (companyDTO.getContact() != null) company.setContact(companyDTO.getContact());

        companyRepository.save(company);

        return true;

    }


}

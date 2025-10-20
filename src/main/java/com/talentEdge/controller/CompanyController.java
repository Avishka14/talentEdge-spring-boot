package com.talentEdge.controller;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.dto.RegistrationResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.service.CompanyServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@CrossOrigin("*")
public class CompanyController {

    private final CompanyServices companyServices;

    public CompanyController(CompanyServices companyServices) {
        this.companyServices = companyServices;
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> companyLogIn(@RequestBody LogInRequest request , HttpServletResponse response){
        return ResponseEntity.ok(companyServices.companyLogIn(request , response));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> companyRegister(@RequestBody CompanyEntity company , HttpServletResponse response){
        return ResponseEntity.ok(companyServices.companyRegister(company , response));
    }

}

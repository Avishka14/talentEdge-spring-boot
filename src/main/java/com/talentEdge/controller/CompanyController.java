package com.talentEdge.controller;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
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

    @GetMapping("/login")
    public ResponseEntity<LogInResponse> companyLogIn(@RequestBody LogInRequest request , HttpServletResponse response){
        return ResponseEntity.ok(companyServices.companyLogIn(request , response));
    }

}

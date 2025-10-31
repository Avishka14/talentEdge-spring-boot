package com.talentEdge.controller;

import com.talentEdge.dto.CompanyDTO;
import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.Response;
import com.talentEdge.dto.RegistrationResponse;
import com.talentEdge.model.CompanyEntity;
import com.talentEdge.service.CompanyServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/company")
@CrossOrigin("*")
public class CompanyController {

    private final CompanyServices companyServices;

    public CompanyController(CompanyServices companyServices) {
        this.companyServices = companyServices;
    }

    @PostMapping("/login")
    public ResponseEntity<Response> companyLogIn(@RequestBody LogInRequest request , HttpServletResponse response){
        return ResponseEntity.ok(companyServices.companyLogIn(request , response));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> companyRegister(@RequestBody CompanyEntity company , HttpServletResponse response){
        return ResponseEntity.ok(companyServices.companyRegister(company , response));
    }

    @PostMapping("/fetch/{id}")
    public ResponseEntity<CompanyDTO> fetchCompanyInfoById(@PathVariable String id){
        try {

            CompanyDTO companyDTO = companyServices.fetchCompanyDataByID(Integer.valueOf(id));
            return ResponseEntity.ok(companyDTO);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> updateCompanyData(@RequestBody CompanyDTO companyDTO){
        try {

            Boolean response = companyServices.updateCompanyData(companyDTO);
            return ResponseEntity.ok(response);

        }catch (Exception e){
            return  ResponseEntity.internalServerError().build();

        }

    }


}

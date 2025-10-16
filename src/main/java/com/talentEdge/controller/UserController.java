package com.talentEdge.controller;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.dto.UserProfileDTO;
import com.talentEdge.model.UserProfile;
import com.talentEdge.service.UserServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin(value = "*" )
public class UserController {

    private UserServices userServices;

    public UserController(UserServices userServices){
        this.userServices = userServices;
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> logIn(@RequestBody LogInRequest request , HttpServletResponse response){
        return ResponseEntity.ok(userServices.logIn(request , response));
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserProfile user , HttpServletResponse Httpresponse) {
        String response = userServices.register(user ,  Httpresponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<UserProfileDTO> fetchUserInfo(@PathVariable("id") String idS){
        try {

            UserProfileDTO dto = userServices.fetchUserInfoById(Integer.parseInt(idS));
            return ResponseEntity.ok(dto);

        }catch(NumberFormatException  e){
            return ResponseEntity.internalServerError().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }



}

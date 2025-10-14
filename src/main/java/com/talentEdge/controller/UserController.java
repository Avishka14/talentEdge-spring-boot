package com.talentEdge.controller;

import com.talentEdge.dto.LogInRequest;
import com.talentEdge.dto.LogInResponse;
import com.talentEdge.model.UserProfile;
import com.talentEdge.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin("*")
public class UserController {

    private UserServices userServices;

    public UserController(UserServices userServices){
        this.userServices = userServices;
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> logIn(@RequestBody LogInRequest request){
        return ResponseEntity.ok(userServices.logIn(request));
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserProfile user) {
        String response = userServices.register(user);
        return ResponseEntity.ok(response);
    }


}

package com.talentEdge.controller;

import com.talentEdge.dto.*;
import com.talentEdge.model.UserProfile;
import com.talentEdge.service.UserServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

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

    @GetMapping("/getuni/{id}")
    public ResponseEntity<UniDTO> fetchUserUni(@PathVariable("id") String idS){

        try {
            UniDTO dto = userServices.fetchUniById(Integer.parseInt(idS));
            return ResponseEntity.ok(dto);

        }catch(NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }


    }

    @GetMapping("/getskills/{id}")
    public ResponseEntity<SkillsDTO> fetchUserSkills(@PathVariable("id") String idS){

        try {
            SkillsDTO dto = userServices.fetchSkilsById(Integer.parseInt(idS));
            return ResponseEntity.ok(dto);

        }catch(NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }

    }

  @PostMapping("/saveskill")
  public ResponseEntity<SkillsDTO> saveUserSkills(@RequestBody SkillsDTO skillsDTO){
        try {

            SkillsDTO skills = userServices.saveSkills(skillsDTO);
            return ResponseEntity.ok(skills);

        } catch (NoSuchElementException e) {
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
  }

    @DeleteMapping("/deleteskills")
    public ResponseEntity<SkillsDTO> updateUserSkills(@RequestBody SkillsDTO dto){
        try {

            SkillsDTO skills = userServices.deleteSkillsById(dto);
            return ResponseEntity.ok(dto);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }

    }

    @GetMapping("/getqualifications/{id}")
    public ResponseEntity<ProfeQualificationsDTO> fetchUserQualifications(@PathVariable("id") String idS){

        try {
            ProfeQualificationsDTO dto = userServices.fetchQualifById(Integer.parseInt(idS));
            return ResponseEntity.ok(dto);

        }catch(NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }

    }

    @PostMapping("/savequalification")
    public ResponseEntity<ProfeQualificationsDTO> saveQualification(@RequestBody ProfeQualificationsDTO profeQualificationsDTO){
        try {

            ProfeQualificationsDTO dto = userServices.saveQualifficatio(profeQualificationsDTO);
            return ResponseEntity.ok(dto);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }

    }

    @DeleteMapping("/deletequalification")
    public ResponseEntity<ProfeQualificationsDTO> deleteQualification(@RequestBody ProfeQualificationsDTO dto){
        try {

            ProfeQualificationsDTO skills = userServices.deleteQualification(dto);
            return ResponseEntity.ok(dto);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }

    }


    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserProfile userProfile){

        try {
            String message = userServices.updateUser(userProfile);
            return ResponseEntity.ok(message);

        }catch (NoSuchElementException e){
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }


    }



}

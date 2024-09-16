package com.examen.security.controller;


import com.examen.security.aggregates.constants.Constants;
import com.examen.security.aggregates.request.PersonaLogInRequest;
import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.LogInResponse;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.entity.PersonaEntity;
import com.examen.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication/v1/")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/registerToUser")
    public ResponseEntity<PersonaResponse> registerToUser(@RequestBody PersonaRequest personaRequest){
        PersonaResponse response = authenticationService.registerToUser(personaRequest);
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/registerToAdmin")
    public ResponseEntity<PersonaResponse> registerToAdmin(@RequestBody PersonaRequest personaRequest){
        PersonaResponse response = authenticationService.registerToAdmin(personaRequest);
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> login(@RequestBody PersonaLogInRequest personaLogInRequest){
        return ResponseEntity.ok(authenticationService.login(personaLogInRequest));
    }
}

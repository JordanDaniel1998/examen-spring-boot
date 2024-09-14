package com.examen.security.controller;

import com.examen.security.aggregates.constants.Constants;
import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personas/v1")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @PostMapping("/create")
    public ResponseEntity<PersonaResponse> createPerson(@RequestBody PersonaRequest personaRequest) {
        PersonaResponse response = personaService.createPerson(personaRequest);
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/searchByDniReniec/{dni}")
    public ResponseEntity<PersonaResponse> createPerson(@PathVariable("dni") String dni) throws Exception {
        PersonaResponse response = personaService.personByDni(dni);
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updatePerson/{dni}")
    public ResponseEntity<PersonaResponse> createPerson(
            @PathVariable("dni") String dni,
            @RequestBody PersonaRequest personaRequest
    ) throws Exception {
        PersonaResponse response = personaService.updatePerson(dni, personaRequest);
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deletePerson/{dni}")
    public ResponseEntity<PersonaResponse> deletePerson(@PathVariable("dni") String dni) {
        PersonaResponse response = personaService.deletePerson(dni);
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listPersons")
    public ResponseEntity<PersonaResponse> listPersons(){
        PersonaResponse response = personaService.listPerson();
        if(response.getCode().equals(Constants.OK_DNI_CODE)){
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}

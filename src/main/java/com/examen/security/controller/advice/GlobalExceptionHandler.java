package com.examen.security.controller.advice;


import com.examen.security.aggregates.constants.Constants;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.controller.customer.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Optional;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PersonaResponse> controllerExceptionGeneral(Exception exception){
        PersonaResponse response = new PersonaResponse(
                Constants.ERROR_TRX_CODE,
                Constants.ERROR_TRX_MESS,
                Optional.empty()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<PersonaResponse> controllerIllegalArgument(IllegalArgumentException illegalArgumentException){
        PersonaResponse response = new PersonaResponse(
                Constants.ERROR_TRX_CODE,
                illegalArgumentException.getMessage(),
                Optional.empty()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<PersonaResponse> controllerResourceNotFound(ResourceNotFoundException resourceNotFoundException){
        PersonaResponse response = new PersonaResponse(
                Constants.ERROR_DNI_CODE_USER,
                resourceNotFoundException.getMessage(),
                Optional.empty()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}

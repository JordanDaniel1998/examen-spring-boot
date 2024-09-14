package com.examen.security.service;

import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.controller.customer.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface PersonaService {
    PersonaResponse createPerson(PersonaRequest personaRequest);
    PersonaResponse personByDni(String dni) throws Exception;
    PersonaResponse updatePerson(String dni, PersonaRequest personaRequest);
    PersonaResponse deletePerson(String dni);
}

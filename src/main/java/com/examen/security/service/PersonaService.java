package com.examen.security.service;

import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.PersonaResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface PersonaService {

    PersonaResponse personByDni(String dni) throws Exception;
    PersonaResponse updatePerson(String dni, PersonaRequest personaRequest);
    PersonaResponse deletePerson(String dni);
    PersonaResponse listPerson();

    UserDetailsService userDetailsService();
}

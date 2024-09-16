package com.examen.security.service;

import com.examen.security.aggregates.request.PersonaLogInRequest;
import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.LogInResponse;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.entity.PersonaEntity;

import java.util.List;

public interface AuthenticationService {
    PersonaResponse registerToUser(PersonaRequest personaRequest);
    PersonaResponse registerToAdmin(PersonaRequest personaRequest);
    LogInResponse login(PersonaLogInRequest personaLogInRequest);
}

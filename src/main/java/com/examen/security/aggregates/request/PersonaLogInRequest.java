package com.examen.security.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaLogInRequest {
    private String email;
    private String password;
}

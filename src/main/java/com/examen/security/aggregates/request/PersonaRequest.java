package com.examen.security.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaRequest {
    private String numDoc;
    private String email;
    private String password;
}

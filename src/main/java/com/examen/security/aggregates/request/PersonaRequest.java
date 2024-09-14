package com.examen.security.aggregates.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaRequest {
    private String numDoc;
    private String nombres;
    private String apellidoPaterno;
}

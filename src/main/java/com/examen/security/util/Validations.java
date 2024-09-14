package com.examen.security.util;

import org.springframework.stereotype.Component;

@Component
public class Validations {
    public Boolean validateDni(String dni) {
        if (!dni.matches("^\\d{8}$")) {
            return true;
        }
        return false;
    }
}

package com.examen.security.util;

import com.examen.security.aggregates.response.ReniecResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

    public static String convertToString(ReniecResponse reniecResponse){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(reniecResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertToObject(String json, Class<T> tipoClase){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, tipoClase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

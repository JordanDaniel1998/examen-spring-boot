package com.examen.security.controller.customer;

public class ResourceNotFoundException extends Exception{
    public ResourceNotFoundException(String message){
        super(message);
    }
}

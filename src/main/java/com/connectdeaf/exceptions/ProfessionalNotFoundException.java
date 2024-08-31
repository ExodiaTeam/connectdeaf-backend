package com.connectdeaf.exceptions;

public class ProfessionalNotFoundException extends RuntimeException {

    public ProfessionalNotFoundException() {
        super("Professional not found.");
    }
}

package com.connectdeaf.exceptions;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException() {
        super("Appointment not found.");
    }
}

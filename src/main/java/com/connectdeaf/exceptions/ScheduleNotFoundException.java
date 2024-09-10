package com.connectdeaf.exceptions;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException() {
        super("Schedule not found.");
    }
}

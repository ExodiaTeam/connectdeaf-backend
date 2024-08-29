package com.connectdeaf.exceptions;

import java.util.UUID;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(UUID serviceId) {
        super("Service not found with ID: " + serviceId);
    }
}

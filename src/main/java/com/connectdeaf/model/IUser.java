package com.connectdeaf.model;

import java.util.UUID;

public interface IUser {
    UUID getId();

    void setId(UUID id);

    String getName();

    void setName(String name);

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);

    String getNumberPhone();

    void setNumberPhone(String numberPhone);
}

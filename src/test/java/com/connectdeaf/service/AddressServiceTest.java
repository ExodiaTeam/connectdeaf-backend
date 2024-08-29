package com.connectdeaf.service;

import com.connectdeaf.domain.address.Address;
import com.connectdeaf.repositories.AddressRepository;
import com.connectdeaf.services.AddressService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository mockAddressRepository;

    private AddressService addressServiceUnderTest;

    @BeforeEach
    void setUp() {
        addressServiceUnderTest = new AddressService(mockAddressRepository);
    }

    @Test
    void testSaveAddress() {
        final Address newAddress = new Address();
        newAddress.setId(UUID.fromString("66e9b65b-ab7b-4e98-94d3-827cd6ea28e0"));
        newAddress.setCep("cep");
        newAddress.setStreet("street");
        newAddress.setNumber("number");
        newAddress.setComplement("complement");

        addressServiceUnderTest.saveAddress(newAddress);

        verify(mockAddressRepository).save(any(Address.class));
    }
}

package com.connectdeaf.infrastructure.service;

import com.connectdeaf.domain.address.Address;
import com.connectdeaf.infrastructure.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public void saveAddress(Address newAddress) {
        addressRepository.save(newAddress);
    }
}

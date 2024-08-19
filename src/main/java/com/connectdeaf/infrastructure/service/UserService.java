package com.connectdeaf.infrastructure.service;

import com.connectdeaf.domain.address.Address;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.infrastructure.controllers.dtos.requests.AddressRequestDTO;
import com.connectdeaf.infrastructure.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AddressService addressService;

    public UserService(UserRepository userRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    @Transactional
    public User createUser(UserRequestDTO userRequestDTO) {
        User newUser = new User();
        newUser.setName(userRequestDTO.name());
        newUser.setEmail(userRequestDTO.email());
        newUser.setPassword(userRequestDTO.password());
        newUser.setPhoneNumber(userRequestDTO.phoneNumber());

        User savedUser = userRepository.save(newUser);

        for (AddressRequestDTO addressRequestDTO : userRequestDTO.addresses()) {
            Address newAddress = getAddress(addressRequestDTO, savedUser);
            addressService.saveAddress(newAddress);
        }

        return savedUser;
    }

    private Address getAddress(AddressRequestDTO addressRequestDTO, User savedUser) {
        Address newAddress = new Address();
        newAddress.setState(addressRequestDTO.state());
        newAddress.setCity(addressRequestDTO.city());
        newAddress.setStreet(addressRequestDTO.street());
        newAddress.setCep(addressRequestDTO.cep());
        newAddress.setNeighborhood(addressRequestDTO.neighborhood());
        newAddress.setComplement(addressRequestDTO.complement());
        newAddress.setNumber(addressRequestDTO.number());
        newAddress.setUser(savedUser);
        return newAddress;
    }

}
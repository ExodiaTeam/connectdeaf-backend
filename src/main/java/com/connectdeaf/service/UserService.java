package com.connectdeaf.service;

import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.address.Address;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.controllers.dtos.requests.AddressRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.exceptions.EmailAlreadyExistsException;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AddressService addressService;

    public UserService(UserRepository userRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.addressService = addressService;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userRequestDTO.email());

        if (userOptional.isPresent()) {
            throw new EmailAlreadyExistsException(userRequestDTO.email());
        }

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

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber()
        );
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

    public UserResponseDTO createUserDTO(UUID userId) {
        User savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber()
        );
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID userId, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existingUser.setEmail(userRequestDTO.email());
        existingUser.setName(userRequestDTO.name());
        existingUser.setPhoneNumber(userRequestDTO.phoneNumber());

        Set<Address> updatedAddresses = new HashSet<>();
        for (AddressRequestDTO addressRequestDTO : userRequestDTO.addresses()) {
            Address newAddress = getAddress(addressRequestDTO, existingUser);
            updatedAddresses.add(newAddress);
            addressService.saveAddress(newAddress);
        }

        existingUser.getAddresses().retainAll(updatedAddresses);

        User updatedUser = userRepository.save(existingUser);

        return new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber()
        );
    }
}
package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.AddressRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.AddressResponseDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.address.Address;
import com.connectdeaf.domain.user.Role;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.EmailAlreadyExistsException;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.repositories.RoleRepository;
import com.connectdeaf.repositories.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, AddressService addressService,
                       BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO, boolean isProfessional) {
        validateEmail(userRequestDTO.email());

        User newUser = buildNewUser(userRequestDTO, isProfessional);
        User savedUser = userRepository.save(newUser);

        saveAddresses(userRequestDTO.addresses(), savedUser);

        return convertToUserResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID userId, UserRequestDTO userRequestDTO) {
        User existingUser = findUserById(userId);

        updateUserDetails(existingUser, userRequestDTO);
        updateAddresses(existingUser, userRequestDTO.addresses());

        User updatedUser = userRepository.save(existingUser);

        return convertToUserResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    public UserResponseDTO createUserDTO(UUID userId) {
        User savedUser = findUserById(userId);
        return convertToUserResponseDTO(savedUser);
    }

    public User findById(UUID userId) {
        return findUserById(userId);
    }

    @Transactional
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<User> findUserByEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }
        return Optional.of(users.get(0));
    }

    private void validateEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (!users.isEmpty()) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private User buildNewUser(UserRequestDTO userRequestDTO, boolean isProfessional) {
        User newUser = new User();
        newUser.setName(userRequestDTO.name());
        newUser.setEmail(userRequestDTO.email());
        newUser.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        newUser.setPhoneNumber(userRequestDTO.phoneNumber());

        Role role = isProfessional ? roleRepository.findByName(Role.Values.ROLE_PROFESSIONAL.name())
                                   : roleRepository.findByName(Role.Values.ROLE_USER.name());
        newUser.setRoles(Set.of(role));

        return newUser;
    }

    private void saveAddresses(List<AddressRequestDTO> addressRequestDTOs, User savedUser) {
        for (AddressRequestDTO addressRequestDTO : addressRequestDTOs) {
            Address newAddress = buildAddress(addressRequestDTO, savedUser);
            addressService.saveAddress(newAddress);
        }
    }

    private void updateUserDetails(User existingUser, UserRequestDTO userRequestDTO) {
        existingUser.setEmail(userRequestDTO.email());
        existingUser.setName(userRequestDTO.name());
        existingUser.setPhoneNumber(userRequestDTO.phoneNumber());
    }

    private void updateAddresses(User existingUser, List<AddressRequestDTO> addressRequestDTOs) {
        Set<Address> updatedAddresses = new HashSet<>();
        for (AddressRequestDTO addressRequestDTO : addressRequestDTOs) {
            Address newAddress = buildAddress(addressRequestDTO, existingUser);
            updatedAddresses.add(newAddress);
            addressService.saveAddress(newAddress);
        }
        existingUser.getAddresses().retainAll(updatedAddresses);
    }

    private Address buildAddress(AddressRequestDTO addressRequestDTO, User user) {
        Address newAddress = new Address();
        newAddress.setState(addressRequestDTO.state());
        newAddress.setCity(addressRequestDTO.city());
        newAddress.setStreet(addressRequestDTO.street());
        newAddress.setCep(addressRequestDTO.cep());
        newAddress.setNeighborhood(addressRequestDTO.neighborhood());
        newAddress.setComplement(addressRequestDTO.complement());
        newAddress.setNumber(addressRequestDTO.number());
        newAddress.setUser(user);
        return newAddress;
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddresses().stream().map(address -> new AddressResponseDTO(
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getCep()))
                        .collect(Collectors.toList()));
    }
}
package com.shopapp.services;

import com.shopapp.component.JwtTokenUtils;
import com.shopapp.dtos.OrderDTO;
import com.shopapp.dtos.UserUpdateDTO;
import com.shopapp.exception.DataNotFoundException;
import com.shopapp.exception.PermissionDenyException;
import com.shopapp.models.Order;
import com.shopapp.models.Role;
import com.shopapp.models.User;
import com.shopapp.repositories.RoleRepository;
import com.shopapp.repositories.UserRepository;
import com.shopapp.response.UserResponse;
import com.shopapp.dtos.UserDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final ModelMapper modelMapper;
    @Override
    public UserResponse createUser(UserDTO userDTO) throws DataNotFoundException, PermissionDenyException {
        String phoneNumber = userDTO.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new DataNotFoundException("Role not found"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You cannot register a admin account");
        }
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

        newUser.setRole(role);
        newUser.setActive(true);
        if(userDTO.getGoogleAccountId() == 0 && userDTO.getFacebookAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        userRepository.save(newUser);
        return UserResponse.builder()
                .id(newUser.getId())
                .active(newUser.isActive())
                .address(newUser.getAddress())
                .googleAccountId(newUser.getGoogleAccountId())
                .phoneNumber(newUser.getPhoneNumber())
                .facebookAccountId(newUser.getFacebookAccountId())
                .dateOfBirth(newUser.getDateOfBirth())
                .fullName(newUser.getFullName())
                .role(newUser.getRole())
                .build();
    }
    @Override
    @Transactional
    public UserResponse updateUser(UserUpdateDTO userUpdateDTO, Long userId) throws DataNotFoundException, PermissionDenyException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the phone number is being updated to an existing one
        String updatedPhoneNumber = userUpdateDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(updatedPhoneNumber)
                && userRepository.existsByPhoneNumber(updatedPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }


        if (userUpdateDTO.getFullName() != null) {
            existingUser.setFullName(userUpdateDTO.getFullName());
        }
        if (updatedPhoneNumber != null) {
            existingUser.setPhoneNumber(updatedPhoneNumber);
        }
        if (userUpdateDTO.getAddress() != null) {
            existingUser.setAddress(userUpdateDTO.getAddress());
        }
        if (userUpdateDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }
        if (userUpdateDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(userUpdateDTO.getFacebookAccountId());
        }
        if (userUpdateDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(userUpdateDTO.getGoogleAccountId());
        }

        // Update password if provided
        String newPassword = userUpdateDTO.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        // Save the updated user
        userRepository.save(existingUser);

        // Build and return the response
        return UserResponse.builder()
                .id(existingUser.getId())
                .active(existingUser.isActive())
                .address(existingUser.getAddress())
                .googleAccountId(existingUser.getGoogleAccountId())
                .phoneNumber(existingUser.getPhoneNumber())
                .facebookAccountId(existingUser.getFacebookAccountId())
                .dateOfBirth(existingUser.getDateOfBirth())
                .fullName(existingUser.getFullName())
                .role(existingUser.getRole())
                .build();
    }
    @Override
    public String[] login(String phoneNumber, String password) throws DataNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("Invalid phone number or password"));
        if(user.getGoogleAccountId() == 0 && user.getFacebookAccountId() == 0) {
            if(!passwordEncoder.matches(password,user.getPassword())) {
                throw new BadCredentialsException("Invalid phone number or password");
            }
        }
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(phoneNumber,password,user.getAuthorities());
            authenticationManager.authenticate(authenticationToken);
            String[] results = new String[2];
            results[0] = jwtTokenUtils.generateToken(user);
            results[1] = user.getRole().getId().toString();
            return results;

    }

    @Override
    public UserResponse getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtils.isTokenExpired(token)) {
            throw new Exception("Token is Expired");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isPresent()) {
            return modelMapper.map(user, UserResponse.class);
        } else {
            throw new Exception("User not found");
        }
    }
}

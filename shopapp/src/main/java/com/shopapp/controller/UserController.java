package com.shopapp.controller;


import com.shopapp.dtos.UserUpdateDTO;
import com.shopapp.response.LoginResponse;
import com.shopapp.response.RegisterResponse;
import com.shopapp.response.UserResponse;
import com.shopapp.services.UserService;
import com.shopapp.dtos.UserDTO;
import com.shopapp.dtos.UserLoginDTO;
import com.shopapp.component.LocalizationUtils;
import com.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final UserService userService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO
           , BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage
                ).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizeMessage(MessageKeys.PASSWORD_NOT_MATCH));
            }
            UserResponse userResponse = userService.createUser(userDTO);
            return ResponseEntity.ok(RegisterResponse.builder()
                            .message(localizationUtils.getLocalizeMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .userResponse(userResponse)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    RegisterResponse.builder()
                    .message(e.getMessage())
                    .build());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            String[] results = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            String token = results[0];
            int roleId = Integer.parseInt(results[1]);
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizeMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                            .token(token)
                            .roleId(roleId)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizeMessage(MessageKeys.LOGIN_FAILED,e.getMessage()))
                            .build()
            );
        }
    }
    @PostMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        try {
            String extractedToken = authHeader.substring(7);
            UserResponse user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("details/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                        @PathVariable("id") Long id,
                                        @RequestHeader("Authorization") String authHeader
                                    , BindingResult result) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage
                ).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userUpdateDTO.getPassword().equals(userUpdateDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizeMessage(MessageKeys.PASSWORD_NOT_MATCH));
            }
            String extractedToken = authHeader.substring(7);
            UserResponse user = userService.getUserDetailsFromToken(extractedToken);
            if(user.getId() != id) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            UserResponse userResponse = userService.updateUser(userUpdateDTO,id);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

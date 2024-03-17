package com.shopapp.services;

import com.shopapp.dtos.UserUpdateDTO;
import com.shopapp.exception.DataNotFoundException;
import com.shopapp.response.UserResponse;
import com.shopapp.dtos.UserDTO;

public interface IUserService {
    UserResponse createUser(UserDTO userDTO) throws Exception;
    String[] login(String phoneNumber, String password) throws DataNotFoundException;
    UserResponse getUserDetailsFromToken(String token) throws Exception;
    UserResponse updateUser(UserUpdateDTO userDTO, Long userId) throws Exception;
}

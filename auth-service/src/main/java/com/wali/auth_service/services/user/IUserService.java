package com.wali.auth_service.services.user;

import com.wali.auth_service.Request.AuthRegisterRequest;
import com.wali.auth_service.dto.response.UserResponseDto;
import com.wali.auth_service.entities.UserEntity;

public interface IUserService {
    public UserEntity createUser(AuthRegisterRequest user);
    public UserResponseDto getUserById(String id);
    UserResponseDto userEntityToUserResponseDto(UserEntity userEntity);
}

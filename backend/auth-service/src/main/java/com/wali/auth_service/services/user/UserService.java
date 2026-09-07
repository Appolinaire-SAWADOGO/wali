package com.wali.auth_service.services.user;

import com.wali.auth_service.Request.AuthRegisterRequest;
import com.wali.auth_service.dto.response.UserResponseDto;
import com.wali.auth_service.entities.UserEntity;
import com.wali.auth_service.enums.UserStatus;
import com.wali.auth_service.exceptions.UserAlreadyExistsException;
import com.wali.auth_service.exceptions.UserNotFoundException;
import com.wali.auth_service.repository.UserRepository;
import com.wali.auth_service.services.kafka.KafkaProducerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Log4j2
@Service
public class UserService implements IUserService {
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public UserEntity createUser(AuthRegisterRequest user) {
        log.debug("Creating user for email={}", user.getEmail());

        boolean userAlreadyExist = userRepository.existsByEmail(user.getEmail());

        if (userAlreadyExist) {
            log.warn("User already exists for email={}", user.getEmail());
            throw new UserAlreadyExistsException("Email " + user.getEmail() + " already exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .passwordHash(passwordEncoder.encode(user.getPassword()))
                .status(UserStatus.ACTIVE)
                .createAt(new Date())
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        log.debug("User created for email={}", user.getEmail());

        kafkaProducerService.sendMessage("user-created", savedUser.getId().toString());

        log.debug("'user-created' topic send, message={}", savedUser.getId());

        return savedUser;
    }

    @Override
    public UserResponseDto getUserById(String id) {
        log.debug("Searching user for userId={}", id);

        UserEntity userEntity = userRepository.findById(UUID.fromString(id)).orElseThrow(
                () ->  {
                    log.warn("User not found for userId={}", id);
                    return  new UserNotFoundException("user id " + id + "  does not exist");
                });

        UserResponseDto userResponseDto = userEntityToUserResponseDto(userEntity);

        log.debug("User found for userId={}", id);

        return userResponseDto;
    }

    @Override
    public UserResponseDto userEntityToUserResponseDto(UserEntity userEntity) {
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .status(userEntity.getStatus())
                .createAt(Date.from(userEntity.getCreateAt().toInstant()))
                .build();

        return userResponseDto;
    }

}

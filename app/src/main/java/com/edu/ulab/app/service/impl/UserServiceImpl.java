package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.DuplicatedException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final Repository<User> userRepository = new Repository<>();
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped userDTO to user: {}", user);
        long id = userRepository.create(user);
        if (id == 0) {
            throw new DuplicatedException("User " + user.getFullName() + " already exist. Cannot add!");
        }
        log.info("Created user with id: {}", id);
        return userMapper.userToUserDto(id, user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped userDTO to user: {}", user);
        ConcurrentHashMap<Long, User> allStorage = userRepository.getAll();
        Optional id = allStorage.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(user))
                .peek(foundedUser -> log.info("User : " + foundedUser.getValue().getFullName() + " has been founded"))
                .map(Map.Entry::getKey)
                .findFirst();

        if (id.isEmpty()) {
            throw new NotFoundException("Users didn't found");
        }
        userDto.setId((Long) id.get());
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getEntity(id);
        if (user == null) {
            throw new NotFoundException("User didn't found");
        }
        log.info("User " + user.getFullName() + " founded");
        return userMapper.userToUserDto(id, user);
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        userRepository.deleteEntity(id);
        log.info("User with ID " + id + " has been deleted");
    }
}

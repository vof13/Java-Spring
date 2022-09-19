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
        long id = userRepository.create(user);
        return userMapper.userToUserDto(id, user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);

        ConcurrentHashMap<Long, User> allStorage = userRepository.getAll();
        Optional id = allStorage.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(user))
                .map(Map.Entry::getKey)
                .findFirst();

        if (id.isEmpty()) {
            throw new NotFoundException("Users didn't found");
        } else userRepository.setEntity((Long) id.get(), user);
        return userMapper.userToUserDto((Long) id.get(), user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getEntity(id);
        if (user == null) {
            throw new NotFoundException("Users didn't found");
        }
        return userMapper.userToUserDto(id, user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteEntity(id);
    }
}

package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.DuplicatedException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped userDTO to user: {}", user);
        userRepository.findAll()
                .forEach(person -> {
                    if (user.equals(person)) {
                        throw new DuplicatedException("User already exist");
                    }
                });
        Person savedUser = userRepository.save(user);
        log.info("Created user with id: {}", savedUser);
        return userMapper.personToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped userDTO to user: {}", user);
        userRepository.findAll()
                .forEach(person -> {
                    if (user.equals(person)) {
                        log.info("User : " + person.getId() + " - " + person.getFullName() + " has been founded");
                        user.setId(person.getId());
                    }
                });
        if (user.getId() == null) {
            throw new NotFoundException("User didn't found");
        }
        return userMapper.personToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
         Person user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User didn't found"));
        log.info("User : " + user.getId() + " - " + user.getFullName() + " has been founded");
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        Person user = userMapper.userDtoToPerson(getUserById(id));
        userRepository.delete(user);
        log.info("User with ID " + id + " has been deleted");
    }
}

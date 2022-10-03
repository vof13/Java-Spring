package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.DuplicatedException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    private final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
    private final String FIND_USER_SQL = "SELECT * FROM PERSON WHERE FULL_NAME=? AND TITLE=? AND AGE=?";
    private final String READ_USER_ID_SQL = "SELECT * FROM PERSON WHERE ID=?";
    private final String DELETE_SQL = "DELETE FROM PERSON WHERE ID=?";
    private final String IS_EXIST_SQL = "SELECT EXISTS(SELECT FROM PERSON WHERE FULL_NAME=? AND AGE=?)";

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public UserDto createUser(UserDto userDto) {
        if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_EXIST_SQL,
                Boolean.class,
                userDto.getFullName(), userDto.getAge()))) {
            throw new DuplicatedException("User already exist");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);
        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("UserDTO created {}", userDto);
        return userDto;
    }

    private UserDto mapperToDto(ResultSet rs) throws SQLException {
        UserDto userDto = new UserDto();
        userDto.setId(rs.getLong(1));
        userDto.setFullName(rs.getString("full_name"));
        userDto.setTitle(rs.getString("title"));
        userDto.setAge(rs.getInt("age"));
        log.info("Person found {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto foundUser;
        try {
            foundUser = jdbcTemplate.queryForObject(
            FIND_USER_SQL,
                    (rs, rowNum) -> mapperToDto(rs),
                     userDto.getFullName(), userDto.getTitle(), userDto.getAge());
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("User not found");
        }
        log.info("User : " + foundUser.getId() + " - " + foundUser.getFullName() + " has been founded");
        return foundUser;
    }

    @Override
    public UserDto getUserById(Long id) {
        UserDto foundUser;
        try {
            foundUser = jdbcTemplate.queryForObject(READ_USER_ID_SQL,
                    (rs, rowNum) -> mapperToDto(rs)
                    , id);
        }catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("User not found");
        }
        log.info("User : " + foundUser.getId() + " - " + foundUser.getFullName() + " has been founded");
        return foundUser;
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        jdbcTemplate.update(DELETE_SQL, id);
        log.info("User with id {} deleted", id);
    }
}

package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
    private final String UPDATE_SQL = "UPDATE PERSON(FULL_NAME, TITLE, AGE) WHERE ID() VALUES (?,?,?,?)";
    private final String READ_SQL = "SELECT * FROM PERSON WHERE ID=?";
    private final String DELETE_SQL = "DELETE FROM PERSON WHERE ID=?";

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
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
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto getUserById(Long id) {
        return  jdbcTemplate.queryForObject(READ_SQL,
                (rs, rowNum) -> {
                    UserDto userDto = new UserDto();
                    userDto.setId(rs.getLong(1));
                    userDto.setFullName(rs.getString("full_name"));
                    userDto.setTitle(rs.getString("title"));
                    userDto.setAge(rs.getInt("age"));
                    return userDto;
                }
                , id);
    }

    @Override
    public void deleteUserById(Long id) {
        jdbcTemplate.update(DELETE_SQL, id);
    }
}

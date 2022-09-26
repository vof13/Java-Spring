package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
    private final String READ_SQL_BY_ID = "SELECT * FROM BOOK WHERE USER_ID=?";

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        // реализовать недстающие методы
        return null;
    }

    @Override
    public BookDto getBookById(Long id) {
        // реализовать недстающие методы
        return null;
    }

    @Override
    public void deleteBookById(Long id) {
        // реализовать недстающие методы
    }

    @Override
    public void deleteBookByUserId(Long id) {

    }

    @Override
    public List<BookDto> getBooksByUserId(Long userID) {
        return jdbcTemplate.query(READ_SQL_BY_ID, (rs, rowNum) -> {
            BookDto bookDto = new BookDto();
            bookDto.setId(rs.getLong("id"));
            bookDto.setUserId(rs.getLong("user_id"));
            bookDto.setTitle(rs.getString("title"));
            bookDto.setAuthor(rs.getString("author"));
            bookDto.setPageCount(rs.getLong("page_count"));
            return bookDto;
        }, userID);
    }
}

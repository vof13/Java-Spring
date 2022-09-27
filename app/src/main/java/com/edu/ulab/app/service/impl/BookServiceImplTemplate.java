package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
    private final String READ_BY_ID_SQL = "SELECT * FROM BOOK WHERE USER_ID=?";
    private final String DELETE_BY_ID_SQL = "DELETE FROM BOOK WHERE ID=?";
    private final String DELETE_BY_USER_ID_SQL = "DELETE FROM BOOK WHERE USER_ID=?";
    private final String GET_BY_ID_SQL = "SELECT * FROM BOOK WHERE ID=?";
    private final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ? WHERE ID = ?";

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
        log.info("BookDTO was created {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
            jdbcTemplate.update(UPDATE_SQL,
                    bookDto.getTitle(),
                    bookDto.getAuthor(),
                    bookDto.getPageCount(),
                    id);
        log.info("The book has been updated {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        BookDto foundBook;
        try {
            foundBook = jdbcTemplate.queryForObject(GET_BY_ID_SQL,
                    (rs, rowNum) ->  mapperToDto(rs)
                    , id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Book not found");
        }
        log.info("Book : " + foundBook.getId() + " - " + foundBook.getTitle() + " was found");
        return  foundBook;
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userID) {
        return jdbcTemplate.query(READ_BY_ID_SQL,
                (rs, rowNum) -> mapperToDto(rs)
                , userID);
    }

    private BookDto mapperToDto(ResultSet rs) throws SQLException {
        BookDto bookDto = new BookDto();
        bookDto.setId(rs.getLong("id"));
        bookDto.setUserId(rs.getLong("user_id"));
        bookDto.setTitle(rs.getString("title"));
        bookDto.setAuthor(rs.getString("author"));
        bookDto.setPageCount(rs.getLong("page_count"));
        log.info("Book found {}", bookDto);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        getBookById(id);
        jdbcTemplate.update(DELETE_BY_ID_SQL, id);
        log.info("Book with ID " + id + " has been deleted");
    }

    @Override
    public void deleteBookByUserId(Long id) {
        jdbcTemplate.update(DELETE_BY_USER_ID_SQL, id);
        log.info("Books with user id {} deleted", id);
    }
}

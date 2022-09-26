package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.BaseWebResponse;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        return createResponse(userBookRequest, createdUser);
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book UPDATE request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);
        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Created user: {}", updatedUser);
        bookService.deleteBookByUserId(updatedUser.getId());
        return createResponse(userBookRequest, updatedUser);

    }
    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user GET WITH ID request: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        List<BookDto> listBookDto = bookService.getBooksByUserId(userId);
        List<Long> bookIdList = listBookDto.stream()
                .filter(Objects::nonNull)
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(BookDto::getId)
                .toList();

        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public ResponseEntity<BaseWebResponse> deleteUserWithBooks(Long userId) {
        log.info("Got user DELETE request: {}", userId);
        userService.deleteUserById(userId);
        bookService.deleteBookByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseWebResponse("User with ID " + userId + " has been deleted"));
    }

    private UserBookResponse createResponse (UserBookRequest userBookRequest, UserDto user) {
        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(user.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(user.getId())
                .booksIdList(bookIdList)
                .build();
    }
}

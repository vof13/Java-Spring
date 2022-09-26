package com.edu.ulab.app.web;

import com.edu.ulab.app.facade.UserDataFacade;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.BaseWebResponse;
import com.edu.ulab.app.web.response.UserBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

import static com.edu.ulab.app.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static com.edu.ulab.app.web.constant.WebConstant.RQID;

/**
 * The UserController provides mapping of the web application.
 * The application is a library catalog. with users and their books that they have
 */
@Slf4j
@RestController
@RequestMapping(value = WebConstant.VERSION_URL + "/user",
        produces = MediaType.APPLICATION_JSON_VALUE)

public class UserController {
    private final UserDataFacade userDataFacade;

    public UserController(UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    /**
     * {@code createUserWithBooks}  creates a user with his books.
     * @return a JSON object with the user ID and the books ID's he has
     */
    @PostMapping(value = "/create")
    @Operation(summary = "Create user book row.",
            responses = {
                    @ApiResponse(description = "User book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse createUserWithBooks(@RequestBody UserBookRequest request,
                                                @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.createUserWithBooks(request);
        log.info("Response with created user and his books: {}", response);
        return response;
    }

    /**
     * {@code updateUserWithBooks}  updates books held by the user
     *
     * @return a JSON object with the user ID and the updated books ID's he has
     */
    @PutMapping(value = "/update")
    @Operation(summary = "Update user book row.",
            responses = {
                    @ApiResponse(description = "User book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse updateUserWithBooks(@RequestBody UserBookRequest request) {
        UserBookResponse response = userDataFacade.updateUserWithBooks(request);
        log.info("Response with updated user and his books: {}", response);
        return response;
    }

    /**
     * {@code getUserWithBooks}  Get User with his books
     *
     * @return a JSON object with the user ID and the books ID's he has
     */
    @GetMapping(value = "/get/{userId}")
    @Operation(summary = "Get user book row.",
            responses = {
                    @ApiResponse(description = "User book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse getUserWithBooks(@PathVariable Long userId) {
        UserBookResponse response = userDataFacade.getUserWithBooks(userId);
        log.info("Response with user and his books: {}", response);
        return response;
    }

    /**
     * {@code ResponseEntity}  Delete User with his books
     *
     * @return a JSON object with text about user delete
     */
    @DeleteMapping(value = "/delete/{userId}")
    @Operation(summary = "Delete user book row.",
            responses = {
                    @ApiResponse(description = "User book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BaseWebResponse.class)))})
    public ResponseEntity<BaseWebResponse> deleteUserWithBooks(@PathVariable Long userId) {
        ResponseEntity<BaseWebResponse> response = userDataFacade.deleteUserWithBooks(userId);
        log.info("Delete user and his books:  userId {}", userId);
        return response;
    }
}

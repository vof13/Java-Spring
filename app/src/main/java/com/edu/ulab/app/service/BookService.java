package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto userDto);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    void deleteBookByUserId (Long id);

    BookDto updateBook(Long id, BookDto bookDto);

    List<BookDto> getBooksByUserId (Long userID);

}

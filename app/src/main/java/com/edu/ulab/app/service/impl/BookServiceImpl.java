package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final Repository<Book> bookRepository = new Repository<>();
    private final BookMapper bookMapper;

    public BookServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoTobook(bookDto);

        long id = bookRepository.create(book);
        return bookMapper.bookToBookDto(id, book);
    }

    @Override
    public void deleteBookByUserId(Long userId) {
        bookRepository.getAll()
                .forEach((K, V) -> {
                    if (Objects.equals(V.getUserId(), userId)) {
                        deleteBookById(K);
                        log.info("Book is deleted: id " + K + " - " + V);
                    }
                });
    }

    @Override
    public BookDto getBookById(Long id) {
        return null;
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        List<BookDto> listBookDto = new ArrayList<>();
        bookRepository.getAll()
                .forEach((K, V) -> {
                    if (Objects.equals(V.getUserId(), userId)) {
                        listBookDto.add(bookMapper.bookToBookDto(K, V));
                    }
                });
        return listBookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteEntity(id);
    }
}

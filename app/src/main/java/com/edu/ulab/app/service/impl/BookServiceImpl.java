package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.DuplicatedException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
        log.info("Mapped bookDTO to book: {}", book);
        long id = bookRepository.create(book);
        if (id == 0) {
            throw new DuplicatedException("Book " + book.getTitle() + " already exist. Cannot add!");
        }
        log.info("Created book with id: {}", id);
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
        Book book = bookRepository.getEntity(id);
        if (book == null) {
            throw new NotFoundException("Book didn't found");
        }
        log.info("Book " + book.getTitle() + " founded");
        return bookMapper.bookToBookDto(id, book);
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
        log.info("Founded " + listBookDto.size() + " user books.");
        return listBookDto;
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        getBookById(id);
        log.info("Book for updating has been founded.");
        Book book = bookMapper.bookDtoTobook(bookDto);
        log.info("Storage has been updated with new book: {}", book);
        bookRepository.setEntity(id, book);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        getBookById(id);
        bookRepository.deleteEntity(id);
        log.info("Book with ID " + id + " has been deleted");
    }
}

package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }



    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoTobook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public void deleteBookByUserId(Long userId) {
        bookRepository.findAll()
                .forEach(book -> {
                    if (Objects.equals(book.getUserId(), userId)) {
                        log.info("Book with ID: " + book.getId() + " has been deleted");
                        bookRepository.delete(book);
                    }
                });
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The book was not found"));
        log.info("Book : " + book.getId() + " - " + book.getTitle() + " was found");
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        List<BookDto> listBookDto = new ArrayList<>();
        bookRepository.findAll()
                .forEach(book -> {
                    if (Objects.equals(book.getUserId(), userId)) {
                        listBookDto.add(bookMapper.bookToBookDto(book));
                    }
                });
        log.info("Founded " + listBookDto.size() + " user books.");
        return listBookDto;
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        getBookById(id);
        log.info("Book with ID: " + id + " for update exists.");
        Book book = bookMapper.bookDtoTobook(bookDto);
        bookDto.setId(id);
        bookRepository.save(book);
        log.info("The book has been updated {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        getBookById(id);
        bookRepository.deleteById(id);
        log.info("Book with ID " + id + " has been deleted");
    }
}

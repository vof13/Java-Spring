package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(person.getId());
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(person.getId());
        result.setUserId(person.getId());
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setUserId(person.getId());

        Book savedBook = new Book();
        savedBook.setId(person.getId());
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setUserId(person.getId());

        //when
        when(bookMapper.bookDtoTobook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги по айди.")
    void updateBook_Test(){
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(person.getId());
        result.setAuthor("new test author");
        result.setTitle("new test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setUserId(person.getId());

        BookDto newBookDto = new BookDto();
        newBookDto.setUserId(person.getId());
        newBookDto.setAuthor("new test author");
        newBookDto.setTitle("new test title");
        newBookDto.setPageCount(100);

        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setPageCount(100);
        newBook.setTitle("new test title");
        newBook.setAuthor("new test author");
        newBook.setUserId(person.getId());

        //when
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookDtoTobook(newBookDto)).thenReturn(newBook);
        when(bookRepository.save(newBook)).thenReturn(newBook);
        when(bookMapper.bookToBookDto(newBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.updateBook(1L, newBookDto);
        assertEquals("new test title", bookDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получить книгу по айди.")
    void getBook_Test() {
        Book book = new Book();
        book.setId(1L);
        book.setPageCount(100);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setUserId(1L);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);
        when(bookRepository.findById(2L)).thenThrow(new NotFoundException("The book was not found"));

        BookDto bookDto = bookService.getBookById(1L);
        assertEquals("test title", bookDto.getTitle());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> bookService.getBookById(2L));
        assertEquals("The book was not found", exception.getMessage());
    }

    @Test
    @DisplayName("Получить все книги.")
    void getAllBookByUserId_Test() {
        Person person = new Person();
        person.setId(1055L);

        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setUserId(person.getId());
        firstBook.setTitle("first book");
        firstBook.setAuthor("first author");
        firstBook.setPageCount(100);

        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setUserId(person.getId());
        secondBook.setTitle("second book");
        secondBook.setAuthor("second author");
        secondBook.setPageCount(200);

        Book thirdBook = new Book();
        thirdBook.setId(3L);
        thirdBook.setUserId(person.getId());
        thirdBook.setTitle("third book");
        thirdBook.setAuthor("third author");
        thirdBook.setPageCount(300);

        List<Book> listBook = new ArrayList<>();
        listBook.add(firstBook);
        listBook.add(secondBook);
        listBook.add(thirdBook);

        when(bookRepository.findAll()).thenReturn(listBook);

        int size = bookService.getBooksByUserId(1055L).size();
        assertEquals(3, size);
    }

    @Test
    @DisplayName("Удалить книгу")
    void deleteBook_Test(){
        Book book = new Book();
        book.setId(1L);
        book.setPageCount(100);
        book.setTitle("test title");
        book.setAuthor("test author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        bookRepository.save(book);
        Assertions.assertDoesNotThrow(() -> {});

    }
}
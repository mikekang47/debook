package com.sihoo.me.debook.controllers;

import com.sihoo.me.debook.applications.BookService;
import com.sihoo.me.debook.domains.Book;
import com.sihoo.me.debook.domains.SortType;
import com.sihoo.me.debook.dto.BookRequestData;
import com.sihoo.me.debook.dto.BookResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> list(@RequestParam String title) {
        return bookService.findBooksByTitle(title);
    }

    @GetMapping("/web")
    public Flux<BookResponse> listWeb(@RequestParam String name, @RequestParam int start,
                                      @RequestParam int display, @RequestParam String sort) {
        return bookService.findBookByWeb(name, start, display, SortType.from(sort).getType());
    }

    @GetMapping("/{id}")
    public Book detail(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Book create(@RequestBody @Valid BookRequestData bookRequestData) {
        return bookService.createBook(bookRequestData);
    }
}

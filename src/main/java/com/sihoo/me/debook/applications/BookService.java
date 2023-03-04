package com.sihoo.me.debook.applications;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.dozermapper.core.Mapper;
import com.sihoo.me.debook.domains.Book;
import com.sihoo.me.debook.dto.BookRequestData;
import com.sihoo.me.debook.dto.BookResponse;
import com.sihoo.me.debook.errors.BookAlreadExistsException;
import com.sihoo.me.debook.errors.BookNotFoundException;
import com.sihoo.me.debook.infra.BookRepository;

import reactor.core.publisher.Flux;

@Service
@Transactional(readOnly = true)
public class BookService {
	private final WebClient webClient;
	private final BookRepository bookRepository;
	private final Mapper mapper;

	@Value("${naver.openapi.client.id}")
	private String id;

	@Value("${naver.openapi.client.secret}")
	private String secret;

	public BookService(BookRepository bookRepository, Mapper mapper) {
		this.webClient = WebClient.create("https://openapi.naver.com/v1/search/book.json");
		this.bookRepository = bookRepository;
		this.mapper = mapper;
	}

	public List<Book> findBooksByTitle(String title) {
		return bookRepository.findAllByTitle(title);
	}

	public Flux<BookResponse> findBookByWeb(String name, int start, int display, String sort) {
		return webClient.get()
			.uri("?query=" + name + "&display=" + display + "&start=" + start + "&sort=" + sort)
			.header("X-Naver-Client-Id", id)
			.header("X-Naver-Client-Secret", secret)
			.retrieve()
			.bodyToFlux(BookResponse.class);
	}

	public Book getBookById(Long id) {
		return bookRepository.findById(id)
			.orElseThrow(() -> new BookNotFoundException(id));
	}

	@Transactional
	public Book createBook(BookRequestData bookRequestData) {
		Optional<Book> source = bookRepository.findByTitle(bookRequestData.getTitle());

		if (source.isPresent()) {
			throw new BookAlreadExistsException(bookRequestData.getTitle(), bookRequestData.getAuthor(),
				bookRequestData.getIsbn());
		}
		Book book = mapper.map(bookRequestData, Book.class);
		return bookRepository.save(book);
	}
}

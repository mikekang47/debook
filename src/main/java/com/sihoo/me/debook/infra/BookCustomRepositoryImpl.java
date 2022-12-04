package com.sihoo.me.debook.infra;

import com.querydsl.jpa.JPQLQueryFactory;
import com.sihoo.me.debook.domains.Book;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sihoo.me.debook.domains.QBook.book;

public class BookCustomRepositoryImpl implements BookCustomRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    public BookCustomRepositoryImpl(JPQLQueryFactory jpqlQueryFactory) {
        this.jpqlQueryFactory = jpqlQueryFactory;
    }

    @Override
    public List<Book> findAllByTitle(String title) {
       return jpqlQueryFactory.selectFrom(book)
               .where(book.title.contains(title))
               .stream()
               .sorted(Comparator.comparing(Book::getTitle))
               .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        return jpqlQueryFactory.selectFrom(book)
                .where(book.title.eq(title))
                .stream().findFirst();
    }
}

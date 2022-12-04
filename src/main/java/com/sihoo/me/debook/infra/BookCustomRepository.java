package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Book;

import java.util.List;
import java.util.Optional;

public interface BookCustomRepository {

    List<Book> findAllByTitle(String title);

    Optional<Book> findByTitle(String title);
}

package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long>, BookCustomRepository {
}

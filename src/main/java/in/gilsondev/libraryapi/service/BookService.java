package in.gilsondev.libraryapi.service;

import in.gilsondev.libraryapi.model.entities.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);
}

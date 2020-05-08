package in.gilsondev.libraryapi.services.impl;

import in.gilsondev.libraryapi.models.repositories.BookRepository;
import in.gilsondev.libraryapi.models.entities.Book;
import in.gilsondev.libraryapi.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}

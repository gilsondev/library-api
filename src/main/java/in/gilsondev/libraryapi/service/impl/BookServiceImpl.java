package in.gilsondev.libraryapi.service.impl;

import in.gilsondev.libraryapi.exception.BusinessException;
import in.gilsondev.libraryapi.model.repositories.BookRepository;
import in.gilsondev.libraryapi.model.entities.Book;
import in.gilsondev.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("ISBN is already exists");
        }
        return repository.save(book);
    }
}

package in.gilsondev.libraryapi.model.repositories;

import in.gilsondev.libraryapi.model.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}

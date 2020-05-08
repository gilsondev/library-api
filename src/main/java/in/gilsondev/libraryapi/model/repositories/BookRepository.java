package in.gilsondev.libraryapi.models.repositories;

import in.gilsondev.libraryapi.models.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

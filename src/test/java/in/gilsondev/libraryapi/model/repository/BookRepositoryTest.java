package in.gilsondev.libraryapi.model.repository;

import in.gilsondev.libraryapi.model.entities.Book;
import in.gilsondev.libraryapi.model.repositories.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Should return true when exists a book with ISBN")
    public void shouldReturnTrueWhenExistsABookWithISBN() {
        Book book = createValidBook();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(book.getIsbn());
        assertThat(exists).isTrue();
    }

    private Book createValidBook() {
        return Book.builder()
                .title("As aventuras")
                .author("Fulano")
                .isbn("123123")
                .build();
    }
}

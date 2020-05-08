package in.gilsondev.libraryapi.services;

import in.gilsondev.libraryapi.models.entities.Book;
import in.gilsondev.libraryapi.models.repositories.BookRepository;
import in.gilsondev.libraryapi.services.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService bookService;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Book book = Book.builder()
                .title("As aventuras")
                .author("Fulano")
                .isbn("123123")
                .build();
        Book savedBook = Book.builder()
                .id(1L)
                .title("As aventuras")
                .author("Fulano")
                .isbn("123123")
                .build();

        when(repository.save(book)).thenReturn(savedBook);

        Book entity = bookService.save(book);

        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(entity.getTitle()).isEqualTo(book.getTitle());
        assertThat(entity.getAuthor()).isEqualTo(book.getAuthor());
    }
}

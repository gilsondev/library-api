package in.gilsondev.libraryapi.service;

import in.gilsondev.libraryapi.exception.BusinessException;
import in.gilsondev.libraryapi.model.entities.Book;
import in.gilsondev.libraryapi.model.repositories.BookRepository;
import in.gilsondev.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        Book book = createValidBook();
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

    @Test
    @DisplayName("Should not save a book with duplicated ISBN")
    public void shouldNotSaveABookWithDuplicatedISBN() {
        Book book = createValidBook();
        String errorMessage = "ISBN is already exists";

        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = catchThrowable(() -> bookService.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage(errorMessage);

        verify(repository, never()).save(book);
    }

    private Book createValidBook() {
        return Book.builder()
                .title("As aventuras")
                .author("Fulano")
                .isbn("123123")
                .build();
    }

}

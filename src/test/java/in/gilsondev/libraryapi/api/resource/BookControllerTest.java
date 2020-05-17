package in.gilsondev.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gilsondev.libraryapi.api.dto.BookDTO;
import in.gilsondev.libraryapi.exception.BusinessException;
import in.gilsondev.libraryapi.model.entities.Book;
import in.gilsondev.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.text.MessageFormat;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
    static String BOOKS_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Should create a book successfully")
    public void createBookTest() throws Exception {
        BookDTO bookDTO = createNewBook();

        Book savedBook = Book.builder()
                .id(1L)
                .author("Autor")
                .title("Meu Livro")
                .isbn("123123123")
                .build();

        given(bookService.save(any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = post(BOOKS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Should catch validation error when create invalid book")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = post(BOOKS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Should catch business error when create book with duplicated ISBN")
    public void createBookWithDuplicatedISBN() throws Exception {
        BookDTO bookDTO = createNewBook();

        String json = new ObjectMapper().writeValueAsString(bookDTO);
        String errorMessage = "ISBN is already exists";
        given(bookService.save(any(Book.class))).willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = post(BOOKS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    @DisplayName("Should fetch informations of book by ID")
    public void getBookDetailTest() throws Exception {
        Long id = 1L;
        final String BOOK_BY_ID_API = MessageFormat.format("{0}/{1}", BOOKS_API, id);

        Book book = Book.builder()
                .id(id)
                .author(createNewBook().getAuthor())
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .build();

        given(bookService.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = get(BOOK_BY_ID_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Should return not found when book not exists")
    public void bookNotExists() throws Exception {
        Long id = 1L;
        final String BOOK_BY_ID_API = MessageFormat.format("{0}/{1}", BOOKS_API, id);

        given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = get(BOOK_BY_ID_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

    }


    @Test
    @DisplayName("Should update a book by ID")
    public void updateBook() throws Exception {
        Long id = 1L;
        final String BOOK_BY_ID_API = MessageFormat.format("{0}/{1}", BOOKS_API, id);

        Book book = Book.builder()
                .id(id)
                .author(createNewBook().getAuthor())
                .title("Old Book")
                .isbn(createNewBook().getIsbn())
                .build();

        Book updatedBook = Book.builder()
                .id(id)
                .author(createNewBook().getAuthor())
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .build();

        String json = new ObjectMapper().writeValueAsString(createNewBook());
        given(bookService.getById(id)).willReturn(Optional.of(book));
        given(bookService.update(any(Book.class))).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = put(BOOK_BY_ID_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(updatedBook.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Should return not found when update book that not exists")
    public void updateBookNotExists() throws Exception {
        Long id = 1L;
        final String BOOK_BY_ID_API = MessageFormat.format("{0}/{1}", BOOKS_API, id);

        given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = put(BOOK_BY_ID_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should remove book by ID")
    public void removeBookByID() throws Exception {
        Long id = 1L;
        final String BOOK_BY_ID_API = MessageFormat.format("{0}/{1}", BOOKS_API, id);

        Book book = Book.builder()
                .id(id)
                .author(createNewBook().getAuthor())
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .build();

        given(bookService.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = delete(BOOK_BY_ID_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Should return not found when remove book that not exists")
    public void deleteBookNotExists() throws Exception {
        Long id = 1L;
        final String BOOK_BY_ID_API = MessageFormat.format("{0}/{1}", BOOKS_API, id);

        given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = delete(BOOK_BY_ID_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());

    }

    private BookDTO createNewBook() {
        return BookDTO.builder()
                .author("Autor")
                .title("Meu Livro")
                .isbn("123123123")
                .build();
    }
}

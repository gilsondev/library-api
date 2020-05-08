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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

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
        given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = post(BOOKS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder()
                .author("Autor")
                .title("Meu Livro")
                .isbn("123123123")
                .build();
    }
}

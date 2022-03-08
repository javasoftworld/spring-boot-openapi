package com.example.app.controller;

import com.example.app.api.LibraryApi;
import com.example.app.model.Book;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


@RestController
@OpenAPIDefinition(
        info = @Info(version = "8.0.0", title = "Library API",
                license = @License(url = "https://www.apache.org/licenses/LICENSE-2.0.txt", name = "Apache 2.0."),
                contact = @Contact(url = "http://dev-api.example.com", name = "API Dev Team")),
        servers = @Server(url = "http://localhost:8080", description = "Development server"))
public class LibraryController implements LibraryApi {

    private List<Book> books = new ArrayList<>();

    {
        Book book = new Book();
        book.setName("Book1");
        book.setBookAuthor("Harry P");

        Book book1 = new Book();
        book1.setName("Book2");
        book1.setBookAuthor("Willy X");
        books.addAll(Arrays.asList(book, book1));
    }

    @Override
    public ResponseEntity<Void> addBookToCollection(@Valid Book body) {

        System.out.println(body.getName());

        books.add(body);

        return ResponseEntity.created(URI.create("/library/books/" + body.getName())).build();
    }

    @Override
    public ResponseEntity<List<Book>> getAllBooksLibrary() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }

    @Override
    public ResponseEntity<Book> getSpecifiedBook(String id) {
        Optional<Book> book = books.stream().filter(book1 -> book1.getName().equalsIgnoreCase(id)).findAny();
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> removeSpecifiedBook(String bookId) {
        books.removeIf(book -> book.getName().equalsIgnoreCase(bookId));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateSpecifiedBook(String bookId, @Valid Book body) {

        AtomicBoolean isUpdated = new AtomicBoolean(false);
        books.stream()
                .filter(book -> book.getName().equalsIgnoreCase(bookId))
                .forEach(book -> {
                    book.setName(body.getName());
                    book.setBookAuthor(body.getBookAuthor());
                    isUpdated.set(true);
                });
        if (isUpdated.get()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}

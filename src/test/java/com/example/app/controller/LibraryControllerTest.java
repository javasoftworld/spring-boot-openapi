package com.example.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllBooks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getSingleBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/Book2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void returnNotFoundForInvalidBookId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/library/books/Book444")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void createNewBookInstance() throws Exception {
        String newBookJSON = "{\"name\": \"Book432\", \"bookAuthor\":\"Jerome K. Jerome\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/library/books")
                        .contentType(MediaType.APPLICATION_JSON).content(newBookJSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andReturn();
    }

    @Test
    void deleteBook() throws Exception {
        String bookId = "Book1";
        mockMvc.perform(MockMvcRequestBuilders.delete("/library/books/{bookId}", bookId))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
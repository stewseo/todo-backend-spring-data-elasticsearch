//package com.example.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(TodoRestController.class)
//class TodoRestControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    void createTodo() {
//    }
//
//    @Test
//    void deleteById() {
//    }
//
//    @Test
//    void deleteAll() {
//    }
//
//    @Test
//    void getById() {
//    }
//
//    @Test
//    void testGetAllTodos() throws Exception {
//
//        assertThrows(NullPointerException.class, () ->
//                mvc.perform(MockMvcRequestBuilders
//                        .get("/todos")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.todos").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.todos[*].id").isNotEmpty())
//        );
//    }
//
//    @Test
//    void patchTodo() {
//    }
//}
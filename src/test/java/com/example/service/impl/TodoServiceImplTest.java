package com.example.service.impl;

import com.example.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

class TodoServiceImplTest {

    @Autowired
    TodoServiceImpl todoService;

    private Todo todo;

    @BeforeEach
    void beforeEach() {
        todo = new Todo(1L, "https://some-url.com/todos", "a todo", true, 1);
    }

    @Test
    void createOrUpdate() throws IOException {
        Todo actual = todoService.createOrUpdate(todo);
    }

    @Test
    void getById() {
        Todo actual = todoService.getById(1L);
    }

    @Test
    void getAll() {
        List<Todo> actual = todoService.getAll();
    }


    @Test
    void deleteById() throws IOException {
        Todo actual = todoService.deleteById(1L);
    }

    @Test
    void patch() {
        Todo actual = todoService.patch(1L, todo);

    }
}
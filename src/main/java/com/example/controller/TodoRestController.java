package com.example.controller;

import com.example.model.Todo;
import com.example.service.impl.TodoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CrossOrigin
@RequestMapping(headers = "Accept=application/json")
@RestController
public class TodoRestController {

    @Autowired
    private TodoServiceImpl service;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo storedTodo = service.createOrUpdate(todo);
        return new ResponseEntity<>(storedTodo, HttpStatus.OK);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Todo> deleteById(@PathVariable(value = "id") Long id) throws IOException {
        Todo result = service.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/todos")
    public ResponseEntity<Todo[]> deleteAll() {
        List<Todo> emptyList = service.deleteAll();
        return new ResponseEntity<>(new Todo[]{}, HttpStatus.OK);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> getById(@PathVariable(value = "id") Long id) {
        Todo todo = service.getById(id);
        return new ResponseEntity<Todo>(todo, HttpStatus.OK);
    }

    @GetMapping("/todos")
    public ResponseEntity<Todo[]> getAll() {
        List<Todo> todos = service.getAll();
        if(service.getAll().isEmpty()) {
            return ResponseEntity.ok(new Todo[]{});
        }
        return new ResponseEntity<>(todos.toArray(Todo[]::new), HttpStatus.OK);
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<Todo> patchTodo(@PathVariable(value = "id") Long id, @RequestBody Todo newTodo) throws IOException {
        Todo todo = service.patch(id, newTodo);
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

}

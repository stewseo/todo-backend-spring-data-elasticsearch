package com.example.controller;

import com.example.model.Todo;
import com.example.service.impl.TodoServiceImpl;
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
        Todo response = service.createOrUpdate(todo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<String> deleteById(@PathVariable(value = "id") Long id) throws IOException {
        String result = service.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/todos")
    public ResponseEntity<Object> deleteAll() {
        Long deleted = service.deleteAll();
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> getById(@PathVariable(value = "id") Long id) {
        Todo todo = service.getById(id);
        return new ResponseEntity<Todo>(todo, HttpStatus.OK);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> getAll() {
        List<Todo> todos = service.getAll();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PatchMapping("/todos/{id}")
    public ResponseEntity<Todo> patchTodo(@PathVariable(value = "id") Long id, @RequestBody Todo newTodo) {
        Todo todo = service.patch(id, newTodo);
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

}

package com.example.controller;

import com.example.model.Todo;
import com.example.model.exceptions.RecordNotFoundException;
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
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Todo> deleteById(@PathVariable(value = "id") Long id) throws IOException {
        Todo deletedTodo = service.deleteById(id);
        return new ResponseEntity<>(deletedTodo, HttpStatus.OK);
    }

    @DeleteMapping("/todos")
    public ResponseEntity<List<Object>> deleteAll() {

        try{

             String createIndexResponse = service.deleteAll();

            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        } catch(RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exception Stub");
        }
    }

    @GetMapping("/todos/{id}")
    public Todo getTodo(@PathVariable(value = "id") Long id) {

        try{
            return service.getById(id);
        } catch(RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exception Stub");
        }
    }

    @GetMapping("/todos")
    public List<Todo> getAllTodos() {
        return service.getAll();
    }


    @PatchMapping("/todos/{id}")
    public Todo patchTodo(@PathVariable(value = "id") Long id, @RequestBody Todo newTodo) {
        return service.patch(id, newTodo);
    }

}

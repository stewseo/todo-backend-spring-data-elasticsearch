package com.example.controller;

import com.example.model.Todo;
import com.example.model.exceptions.RecordNotFoundException;
import com.example.service.impl.TodoServiceImpl;
import jakarta.json.JsonArray;
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

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@CrossOrigin(
        methods = {POST, GET, OPTIONS, DELETE, PATCH},
        maxAge = 3600,
        allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
        origins = "*"
)
@RequestMapping(headers = "Accept=application/json")
@RestController
public class TodoRestController {

    @Autowired
    private TodoServiceImpl repo;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo response = repo.createOrUpdate(todo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Todo> deleteById(@PathVariable(value = "id") Long id) throws IOException {
        Todo deletedTodo = repo.deleteById(id);
        return new ResponseEntity<>(deletedTodo, HttpStatus.OK);
    }


    @DeleteMapping("/todos")
    public ResponseEntity<String> deleteAll() {

        try{

             String createIndexResponse = repo.deleteAll();

            return new ResponseEntity<>(JsonArray.EMPTY_JSON_ARRAY.toString(), HttpStatus.OK);
        } catch(RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exception Stub");
        }
    }

    @GetMapping("/todos/{id}")
    public Todo getTodo(@PathVariable(value = "id") Long id) {

        try{
            return repo.getById(id);
        } catch(RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exception Stub");
        }
    }

    @GetMapping("/todos")
    public List<Todo> getAllTodos() {
        return repo.getAll();
    }


    @PatchMapping("/todos/{id}")
    public Todo patchTodo(@PathVariable(value = "id") Long id, @RequestBody Todo newTodo) {
        return repo.patch(id, newTodo);
    }

}

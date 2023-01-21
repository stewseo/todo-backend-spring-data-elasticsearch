package com.example.controller;

import co.elastic.clients.elasticsearch._types.Result;
import com.example.model.Todo;
import com.example.model.exceptions.InvalidTodoException;
import com.example.connector.ESClientConnector;
import com.example.service.TodoService;
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
import java.util.List;

@CrossOrigin
@RequestMapping(headers = "Accept=application/json")
@RestController
public class TodoRestController {

    @Autowired
    private TodoService<Todo, Long> repo;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) throws IOException {
        Todo response = (Todo) repo.createOrUpdate(todo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Todo> deleteTodo(@PathVariable(value = "id") Long id) throws IOException {
        Todo deletedTodo = (Todo) repo.deleteById(id);
        return new ResponseEntity<>(deletedTodo, HttpStatus.OK);
    }

    @DeleteMapping("/todos")
    public ResponseEntity<Object> deleteAllTodos() {

        try{
            Long deleted = (Long) repo.deleteAll();
            return new ResponseEntity<>(deleted, HttpStatus.OK);
        } catch(InvalidTodoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exception Stub");
        }
    }

    @GetMapping("/todos/{id}")
    public Todo getTodo(@PathVariable(value = "id") Long id) {

        try{
            return repo.getById(id);
        } catch(InvalidTodoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exception Stub");
        }
    }

    @GetMapping("/todos")
    public List<Todo> getAllTodos() throws IOException {
        return repo.getAll();
    }


    @PatchMapping("/todos/{id}")
    public Todo patchTodo(@PathVariable(value = "id") Long id, @RequestBody Todo newTodo) {
        return repo.patch(id, newTodo);
    }

}

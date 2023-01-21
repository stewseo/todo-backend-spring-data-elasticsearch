package com.example.service;

import com.example.connector.ESClientConnector;
import com.example.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class TodoServiceImpl implements TodoService<Todo, Long> {

    @Autowired
    private ESClientConnector esClientConnector;

    @Override
    public Todo createOrUpdate(Todo todo) throws IOException {
        return esClientConnector.createOrUpdate(todo);
    }

    @Override
    public Todo getById(Long id) {
        return esClientConnector.getById(id);
    }

    @Override
    public List<Todo> getAll() throws IOException {
        return esClientConnector.getAll();
    }

    @Override
    public String deleteAll() {
        return Objects.requireNonNull(esClientConnector.deleteAllTodos().deleted()).toString();
    }

    @Override
    public String deleteById(Long id) {
        return esClientConnector.deleteById(id).toString();
    }

    @Override
    public Todo patch(Long id, Todo todo) {
        return esClientConnector.patch(id, todo);
    }
}

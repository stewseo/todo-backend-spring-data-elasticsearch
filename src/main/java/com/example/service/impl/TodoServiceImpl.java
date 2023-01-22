package com.example.service.impl;

import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import com.example.connector.ESClientConnector;
import com.example.model.Todo;
import com.example.model.exceptions.InvalidTodoException;
import com.example.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoServiceImpl implements TodoService<Todo, Long> {

    @Autowired
    private ESClientConnector esClientConnector;

    private final AtomicLong counter = new AtomicLong();

    private static final String DEFAULT_TITLE = "a todo";

    private static final String DEFAULT_URL = "https://todo-java.herokuapp.com:443/todos";

    @Override
    public Todo createOrUpdate(Todo todo) {
        try {

            if (todo.getId() == null) {
                final Long id = counter.getAndIncrement();
                todo.setId(id);
            }

            if (todo.getUrl() == null) {
                String urlString = DEFAULT_URL + "/" + todo.getId();
                todo.setUrl(urlString);
            }
            if (todo.getTitle() == null) {
                todo.setTitle(DEFAULT_TITLE);
            }
            esClientConnector.createOrUpdate(todo);
            return todo;

        } catch (InvalidTodoException e) {
            throw new InvalidTodoException("Todo is missing required fields {}", e);
        }
    }

    @Override
    public Todo getById(Long id) {
        return esClientConnector.getById(id);
    }

    @Override
    public List<Todo> getAll() {
        return esClientConnector.getAll();
    }

    @Override
    public DeleteByQueryResponse deleteAll() {
        return esClientConnector.deleteAll();
    }

    @Override
    public Todo deleteById(Long id) throws IOException {
        Todo todo = esClientConnector.getById(id);
        if(todo != null) {
            esClientConnector.deleteById(id);
            return todo;
        }
        return null;
    }

    @Override
    public Todo patch(Long id, Todo todo) {
        return esClientConnector.patch(id, todo);
    }
}

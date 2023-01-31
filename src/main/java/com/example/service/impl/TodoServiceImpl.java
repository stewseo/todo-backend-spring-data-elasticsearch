package com.example.service.impl;

import com.example.connector.ESClientConnector;
import com.example.model.Todo;
import com.example.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TodoServiceImpl implements TodoService<Todo, Long> {

    @Autowired
    private ESClientConnector esClientConnector;

    private final String API_BASE_URL = "https://spring-data-elasticsearch.herokuapp.com/todos";

    @Override
    public Todo createOrUpdate(Todo todo) {

        setTodo(todo);
        esClientConnector.createOrUpdate(todo);
        return todo;

    }

    private void setTodo(Todo todo) {
        long id = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        todo.setId(id);

        String url = API_BASE_URL + "/" + id;
        todo.setUrl(url);

        todo.setCompleted(false);

        if(todo.getOrder() != null) {
            todo.setOrder(todo.getOrder());
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
    public List<Todo> deleteAll() {
        return esClientConnector.deleteAll();
    }

    @Override
    public Todo deleteById(Long id) throws IOException {
        return esClientConnector.deleteById(id);
    }

    @Override
    public Todo patch(Long id, Todo patchWith) throws IOException {

        Todo todo = esClientConnector.getById(id);

        patchTodo(todo, patchWith);

        return esClientConnector.patch(todo);
    }

    private void patchTodo(Todo todo, Todo patchWith) {

        String title = patchWith.getTitle();
        if (title != null) {
            todo.setTitle(title);
        }
        Integer order = patchWith.getOrder();
        if (order != null) {
            todo.setOrder(order);
        }
        Boolean completed = patchWith.getCompleted();
        if (completed != null) {
            todo.setCompleted(true);
        }

    }

}

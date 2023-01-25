package com.example.service.impl;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.example.connector.ESClientConnector;
import com.example.model.Todo;
import com.example.model.exceptions.RecordNotFoundException;
import com.example.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoServiceImpl implements TodoService<Todo, Long> {

    @Autowired
    private ESClientConnector esClientConnector;

    private Map<Long, Todo> todos = new ConcurrentHashMap<>();

    @Override
    public Todo createOrUpdate(Todo todo) {

        try {

            setTodo(todo);

            return esClientConnector.createOrUpdate(todo);

        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Todo is missing required fields {}", e);
        }
    }

    private final String API_BASE_PATH = "https://spring-data-elasticsearch.herokuapp.com/todos";

    private static final AtomicLong atomicLong = new AtomicLong();

    private void setTodo(Todo todo) {

        long id = atomicLong.getAndIncrement();
        if (todo.getId() == null) {
            todo.setId(id);
        }
        if (todo.getUrl() == null) {
            todo.setUrl(API_BASE_PATH + "/" + id);
        }
        if (todo.getCompleted() == null || todo.getCompleted()) {
            todo.setCompleted(false);
        }
        todos.put(id, todo);
    }

    @Override
    public Todo getById(Long id) {
        todos.remove(id);
        return esClientConnector.getById(id);
    }

    @Override
    public List<Todo> getAll() {

        if (todos.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            esClientConnector.getAll();
            return todos.values().stream().toList();
        }
    }

    @Override
    public Long deleteAll() {
        todos.clear();
        return esClientConnector.deleteAll();
    }

    @Override
    public String deleteById(Long id) throws IOException {
        todos.remove(id);
        return esClientConnector.deleteById(id);

    }

    @Override
    public Todo patch(Long id, Todo todo) {

        Todo patchTo = esClientConnector.getById(id);

        patchWith(patchTo, todo);

        return esClientConnector.patch(id, patchTo);
    }

    public void clearIndex() {
        TypeMapping typeMapping = esClientConnector.getTypeMapping();
        esClientConnector.deleteIndex();
        CreateIndexResponse indexResponse = esClientConnector.createIndex(typeMapping);
    }

    public void patchWith(Todo todo, Todo patchWith) {

        if(patchWith.getOrder() != null) {
            todo.setOrder(patchWith.getOrder());
        }

        if(patchWith.getTitle() != null) {
            todo.setTitle(patchWith.getTitle());
        }

        if(patchWith.getCompleted() != null) {
            todo.setCompleted(true);
        }

    }
}

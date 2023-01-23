package com.example.service.impl;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
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

    private final Map<Long, Todo> todos = new ConcurrentHashMap<>();

    @Override
    public Todo createOrUpdate(Todo todo) {
        try {
            long id = esClientConnector.getId();

            todo.setId(id);

            if (todo.getUrl() == null) {
                String urlString = "https://todo-backend-spring-data-elasticsearch.herokuapp.com:443/todos/" + id;
                todo.setUrl(urlString);
            }

            esClientConnector.createOrUpdate(todo);
            todos.put(id, todo);

            return todo;

        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Todo is missing required fields {}", e);
        }
    }

    @Override
    public Todo getById(Long id) {
        return esClientConnector.getById(id);
    }

    @Override
    public List<Todo> getAll() {
        return todos.values().stream().toList();
    }

    @Override
    public String deleteAll() {

        TypeMapping typeMapping = esClientConnector.getTypeMapping();
        esClientConnector.deleteIndex();
        CreateIndexResponse indexResponse = esClientConnector.createIndex(typeMapping);
        todos.clear();
        return indexResponse.toString();
    }

    @Override
    public Todo deleteById(Long id) throws IOException {
        DeleteResponse deleteResponse = esClientConnector.deleteById(id);
        return todos.remove(id);
    }

    @Override
    public Todo patch(Long id, Todo todo) {
        Todo updated = esClientConnector.patch(id, todo);
        return todos.replace(id, updated);
    }
}

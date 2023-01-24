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
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService<Todo, Long> {

    @Autowired
    private ESClientConnector esClientConnector;
    private final Map<Long, Todo> todos = new ConcurrentHashMap<>();
    private final AtomicLong atomicLong = new AtomicLong();
    private final String API_BASE_PATH = "https://todo-spring-data-elasticsearch.herokuapp.com/todos";

    @Override
    public Todo createOrUpdate(Todo todo) {

        try {
            long id = atomicLong.getAndIncrement();

            buildTodo(id, todo);

            esClientConnector.createOrUpdate(todo);
            todos.put(id, todo);
            return todo;
        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Todo is missing required fields {}", e);
        }
    }

    private void buildTodo(long id, Todo todo) {
        todo.setId(id);
        todo.setUrl(API_BASE_PATH+"/"+id);
        todo.setCompleted(false);

    }

    @Override
    public Todo getById(Long id) {
//        esClientConnector.getById(id);
        return todos.get(id);
    }

    @Override
    public List<Todo> getAll() {
        return todos.values().stream().collect(Collectors.toList());
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

        Todo patchTo = todos.get(id);

        patchWith(patchTo, todo);
        
        return esClientConnector.patch(id, patchTo);
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

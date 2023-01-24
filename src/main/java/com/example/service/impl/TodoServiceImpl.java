package com.example.service.impl;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
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

//    private final Map<Long, Todo> todos = new ConcurrentHashMap<>();

    @Override
    public Todo createOrUpdate(Todo todo) {

        try {

            buildTodo(todo);

            return esClientConnector.createOrUpdate(todo);

        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Todo is missing required fields {}", e);
        }
    }

    private final String API_BASE_PATH = "https://todo-spring-data-elasticsearch.herokuapp.com/todos";

    private final AtomicLong atomicLong = new AtomicLong();

    private void buildTodo(Todo todo) {
        long id = atomicLong.getAndIncrement();

        todo.setId(id);
        if(todo.getId() == null) {

        }
        todo.setUrl(API_BASE_PATH + "/" + id);
        todo.setCompleted(false);
    }


    @Override
    public Todo getById(Long id) {
        return esClientConnector.getById(id);
    }

    @Override
    public List<Todo> getAll() {
        return esClientConnector.getAll().size() == 0 ? Collections.emptyList() : esClientConnector.getAll();

    }

    @Override
    public String deleteAll() {

        long deleted = esClientConnector.deleteAll();

        return String.valueOf(deleted);
    }

    @Override
    public Todo deleteById(Long id) throws IOException {
        Todo todo = esClientConnector.getById(id);
        esClientConnector.getById(Long.valueOf(id));
        return todo;

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

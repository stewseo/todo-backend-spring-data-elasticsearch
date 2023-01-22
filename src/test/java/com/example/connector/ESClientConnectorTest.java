package com.example.connector;

import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.model.TestTodo;
import com.example.model.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ESClientConnectorTest {

    @Autowired
    private ESClientConnector esClientConnector;

    @Test
    void testCreateOrUpdate() {
        Todo todo = TestTodo.generateValidTodo();
        long id = todo.getId();
        IndexResponse response = esClientConnector.createOrUpdate(todo);
        assertThat(response.result().name()).isIn( "Created","Updated");

        assertThat(response.result().jsonValue()).isIn("created","updated");
        assertThat(response.id()).isEqualTo(String.valueOf(id));

    }

    @Test
    void testDeleteById() throws IOException {
        Todo todo = TestTodo.generateValidTodo();
        DeleteResponse response = esClientConnector.deleteById(todo.getId());
        assertThat(response.result().name()).isIn("Deleted");
        assertThat(response.result().jsonValue()).isEqualTo("deleted");
    }

    @Test
    void testGetById() {
        Todo todo = TestTodo.generateValidTodo();
        esClientConnector.createOrUpdate(todo);
        Todo res = esClientConnector.getById(todo.getId());
        assertThat(res.getId()).isNotNull();
        assertThat(res.getTitle()).isEqualTo("a todo");
    }

    @Test
    void testGetAll() {

        Todo todo = TestTodo.generateValidTodo();
        esClientConnector.createOrUpdate(todo);
        List<Todo> res = esClientConnector.getAll();
        assertThat(res.size()).isEqualTo(2);
        assertThat(res).contains(todo);
    }

    @Test
    void testDeleteAllTodos() {
        Todo todo = TestTodo.generateValidTodo();
        esClientConnector.createOrUpdate(todo);
        DeleteByQueryResponse response = esClientConnector.deleteAll();
        assertThat(response.deleted()).isEqualTo(1L);
    }


    @Test
    void testPatch() {
        Todo todo = TestTodo.generateValidTodo();
        assertThat(todo.getTitle()).isEqualTo("a todo");
        esClientConnector.createOrUpdate(todo);
        Todo patchWith = TestTodo.generatePatchWithTodo();
        assertThat(patchWith.getTitle()).isEqualTo("patched todo");
        Todo patched = esClientConnector.patch(todo.getId(), patchWith);
        assertThat(patched.getTitle()).isEqualTo(patchWith.getTitle());
        Todo result = esClientConnector.getById(todo.getId());
        assertThat(result.getUrl()).isEqualTo("https://url/todos/1");
        assertThat(result.getId()).isEqualTo(todo.getId());

    }
}
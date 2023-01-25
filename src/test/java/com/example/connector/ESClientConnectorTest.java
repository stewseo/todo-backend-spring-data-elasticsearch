//package com.example.connector;
//
//import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
//import co.elastic.clients.elasticsearch.core.DeleteResponse;
//import co.elastic.clients.elasticsearch.core.IndexResponse;
//import com.example.model.TestTodo;
//import com.example.model.Todo;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class ESClientConnectorTest {
//
//    @Autowired
//    private ESClientConnector esClientConnector;
//
//    @Test
//    void testCreateOrUpdate() {
//        Todo todo = new Todo("title", "url");
//
//        String response = esClientConnector.createOrUpdate(todo);
//
//        assertThat(response.result().name()).isIn( "Created","Updated");
//
//        assertThat(response.result().jsonValue()).isIn("created","updated");
//        assertThat(response.id()).isEqualTo(String.valueOf(id));
//
//    }
//
//    @Test
//    void testDeleteById() throws IOException {
//        Todo todo = new Todo();
//
//        DeleteResponse delResp = esClientConnector.deleteById(todo.getId());
//        assertThat(delResp.result().name()).isIn("Deleted");
//        assertThat(delResp.result().jsonValue()).isEqualTo("deleted");
//    }
//
//    @Test
//    void testGetById() {
//        Todo todo = new Todo();
//        esClientConnector.createOrUpdate(todo);
//        Todo res = esClientConnector.getById(todo.getId());
//        assertThat(res.getId()).isNotNull();
//        assertThat(res.getTitle()).isEqualTo("a todo");
//    }
//
//    @Test
//    void testGetAll() {
//
//        Todo todo = new Todo();
//        esClientConnector.createOrUpdate(todo);
//        List<Todo> res = esClientConnector.getAll();
//        assertThat(res.size()).isEqualTo(2);
//        assertThat(res).contains(todo);
//    }
//
//    @Test
//    void testPatch() {
//        Todo todo = new Todo();
//        assertThat(todo.getTitle()).isEqualTo("a todo");
//        esClientConnector.createOrUpdate(todo);
//        Todo patchWith = new Todo();
//        assertThat(patchWith.getTitle()).isEqualTo("patched todo");
//        Todo patched = esClientConnector.patch(todo.getId(), patchWith);
//        assertThat(patched.getTitle()).isEqualTo(patchWith.getTitle());
//        Todo result = esClientConnector.getById(todo.getId());
//        assertThat(result.getUrl()).isEqualTo("https://url/todos/1");
//        assertThat(result.getId()).isEqualTo(todo.getId());
//
//    }
//}
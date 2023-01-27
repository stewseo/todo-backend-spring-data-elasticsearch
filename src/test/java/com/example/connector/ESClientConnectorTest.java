package com.example.connector;

import com.example.model.Todo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static com.example.test_vars.TestVars.ID;
import static com.example.test_vars.TestVars.ORDER;
import static com.example.test_vars.TestVars.TITLE;
import static com.example.test_vars.TestVars.URL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ESClientConnectorTest {

    @Autowired
    private ESClientConnector esClientConnector;

    @BeforeEach
    void beforeEach() {
        for (Long id : List.of(1L, 2L, 3L)) {
            esClientConnector.createOrUpdate(new Todo(id, URL + "/" + id, "a todo", id.intValue(), false));
        }
    }

    @AfterEach
    void afterEach() {
        esClientConnector.deleteAll();
    }

    @Test
    void testGetAll() {
        assertThat(esClientConnector.getAll().size()).isEqualTo(3);
    }

    @Test
    void deleteDocumentTest() {
        esClientConnector.deleteAll();
        assertThat(esClientConnector.docsCount()).isEqualTo(0);
        assertThat(esClientConnector.getAll().size()).isEqualTo(0);
    }

    @Test
    void testGetById() {
        assertThat(esClientConnector.getById(1L).toString()).isEqualTo("1");
        assertThat(esClientConnector.getById(2L).toString()).isEqualTo("2");
        assertThat(esClientConnector.getById(3L).toString()).isEqualTo("3");
        assertThat(esClientConnector.getById(4L).toString()).isEqualTo("4");

    }



//        Todo actual = esClientConnector.createOrUpdate(todo);
//        assertThat(esClientConnector.idExists(ID)).isTrue();

//        assertThat(actual.getId()).isExactlyInstanceOf(Long.class);
//        assertThat(actual.getTitle()).isEqualTo("http://expected-url.com:8080");
//        Todo todo = esClientConnector.getAll(this.todo.getId());



    @Test
    void testDeleteAll() {
//        Todo todo = esClientConnector.deleteAll(this.todo.getId());

    }

    @Test
    void deleteById() throws IOException {
//        Todo todo = esClientConnector.deleteById(this.todo.getId());

    }

    @Test
    void testPatch() throws IOException {
//        Todo actual = esClientConnector.patch(todo);

    }

    @Test
    void testUpdateByQuery() throws IOException {


    }
}

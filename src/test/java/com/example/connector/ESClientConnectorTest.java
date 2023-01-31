package com.example.connector;

import com.example.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.test_vars.TestVars.URL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ESClientConnectorTest {

    @Autowired
    private ESClientConnector esClientConnector;

    private static final Logger logger = LoggerFactory.getLogger(ESClientConnectorTest.class);

    private final AtomicLong docsCount = new AtomicLong();

    private final int size = 10;

    @BeforeEach
    void beforeEach() {

        List<Long> list = ThreadLocalRandom.current().longs( 0 , 100000 ).limit(size).boxed().toList();

        try {

            esClientConnector.deleteAll();

            Thread.sleep(3000);

            docsCount.set(esClientConnector.docsCount());
            System.out.println("docsCount: " + docsCount);

            assertThat(docsCount.get()).isEqualTo(0);

            for (Long id : list) {
                esClientConnector.createOrUpdate(new Todo(id, URL + "/" + id, "a todo", id.intValue(), false));
            }

            Thread.sleep(3000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAll() {
        List<Todo> todos = esClientConnector.getAll();
        assertThat(todos.size()).isEqualTo(size);
    }

    @Test
    void testDocsCount() {
        long docsCount = esClientConnector.docsCount();
        assertThat(docsCount).isEqualTo(size);
    }

    @Test
    void testGetById() {
        List<Todo> todos = esClientConnector.getAll();
        for (Todo todo : todos) {
            long id = todo.getId();
            assertThat(esClientConnector.getById(id)).hasNoNullFieldsOrProperties();
        }

    }

    @Test
    void testDeleteById() throws IOException, InterruptedException {
        List<Todo> todos = esClientConnector.getAll();
        AtomicLong i = new AtomicLong(todos.size());

        for (Todo todo : todos) {
            esClientConnector.deleteById(todo.getId());
            i.getAndDecrement();
        }
        Thread.sleep(5000);
        assertThat(i.get()).isEqualTo(0);
        assertThat(esClientConnector.docsCount()).isEqualTo(i.get());

    }

    @Test
    void testPatch() throws IOException {
        List<Todo> todos = esClientConnector.getAll();
        for (Todo todo : todos) {
            assertThat(todo.getTitle()).isEqualTo("a todo");
            assertThat(todo.getCompleted()).isFalse();

            todo.setTitle("updated todo");
            todo.setCompleted(true);

            Todo actual = esClientConnector.patch(todo);
            assertThat(actual.getTitle()).isEqualTo("updated todo");
            assertThat(actual.getCompleted()).isTrue();
        }

    }
}

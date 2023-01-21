package com.example.connector;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.WriteResponseBase;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import com.example.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ESClientConnector {

    private static final Logger logger = LoggerFactory.getLogger(ESClientConnector.class);

    private final String indexName = "todo-index";

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    public Todo createOrUpdate(Todo todo) {
        try {
            elasticsearchAsyncClient.index(i -> i
                    .index(indexName)
                    .id(String.valueOf(todo.getId()))
                    .document(todo)
            ).whenComplete((resp, exception) -> {
                if (exception != null) {
                    logger.error("Failed to index", exception);
                } else {
                    logger.info("Indexed successfully");
                }
            }).thenApply(WriteResponseBase::result
            ).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return todo;
    }

    public Result deleteTodo(Long id) throws IOException {

        try {
            return elasticsearchAsyncClient.delete(d -> d
                    .index(indexName)
                    .id(String.valueOf(id))
            ).whenComplete((resp, exception) -> {
                if (exception != null) {
                    logger.error("Failed to index", exception);
                } else {
                    logger.info("Indexed successfully");
                }
            }).thenApply(DeleteResponse::result).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }


    public Todo getById(Long id) {

        try {
            return elasticsearchAsyncClient.get(g -> g
                            .index(indexName)
                            .id(String.valueOf(id)
                            )
                    , Todo.class
            ).whenComplete((resp, exception) -> {
                if (exception != null) {
                    logger.error("Failed to index", exception);
                } else {
                    logger.info("Indexed successfully");
                }
            }).thenApply(GetResult::source).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Todo> getAll() {
        return elasticsearchAsyncClient.search(s -> s
                                .index(indexName)
                        , Todo.class
                ).whenComplete((resp, exception) -> {
                    if (exception != null) {
                        logger.error("Failed to index", exception);
                    } else {
                        logger.info("Indexed successfully");
                    }
                })
                .thenApply(SearchResponse::hits)
                .thenApply(HitsMetadata::hits)
                .join()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList()
                );

    }

    private final String TIMESTAMP_FIELD = "timestamp";

    private final JsonData MIN_TIMESTAMP_VALUE = JsonData.of(0);

    private final Query ALL_TIMESTAMP_QUERY = Query.of(q -> q
            .range(r -> r
                    .field(TIMESTAMP_FIELD)
                    .gte(MIN_TIMESTAMP_VALUE))
    );


    public DeleteByQueryResponse deleteAllTodos() {
        try {
            return elasticsearchAsyncClient.deleteByQuery(d -> d
                    .query(ALL_TIMESTAMP_QUERY)
                    .index(indexName)
            ).whenComplete((resp, exception) -> {
                if (exception != null) {
                    logger.error("Failed to index", exception);
                } else {
                    logger.info("Indexed successfully");
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public Object deleteById(Long id) {
        return null;
    }

    public Todo patch(Long id, Todo newTodo) {

        Todo update = newTodo.patchTodo();

        try {
            elasticsearchAsyncClient.update(u -> u
                            .index(indexName)
                            .doc(update)
                    , Todo.class
            ).whenComplete((resp, exception) -> {
                if (exception != null) {
                    logger.error("Failed to index", exception);
                } else {
                    logger.info("Indexed successfully");
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return update;
    }

}

package com.example.connector;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.JsonData;
import com.example.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ESClientConnector {

    private static final Logger logger = LoggerFactory.getLogger(ESClientConnector.class);

    private final String indexName = "todo-index";

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    public IndexResponse createOrUpdate(Todo todo) {
        try {


            return elasticsearchAsyncClient.index(i -> i
                    .index(indexName)
                    .id(String.valueOf(todo.getId()))
                    .pipeline("timestamp-pipeline")
                    .document(todo)
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

    public DeleteResponse deleteById(Long id) throws IOException {
//        return elasticsearchAsyncClient.deleteByQuery(d -> d
//                .query(q -> q
//                        .match(m -> m
//                .field("id")
//                .query(id)))).get();
//
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
            }).get();

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
                .join()
                .hits()
                .hits()
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

    public Todo patch(Long id, Todo newTodo) {

        Todo update = newTodo.patchTodo();

        return Objects.requireNonNull(elasticsearchAsyncClient.update(u -> u
                                .index(indexName)
                                .id(String.valueOf(id))
                                .doc(update)
                        , Todo.class
                ).whenComplete((resp, exception) -> {
                    if (exception != null) {
                        logger.error("Patch Unsuccessful", exception);
                    } else {

                        logger.info("Patch Successful");
                    }
                })
                .join()
                .get()
                )
                .source();
    }

    public DeleteIndexResponse deleteIndex() {

        try {
            return elasticsearchAsyncClient.indices().delete(d -> d.index(indexName)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getId() {

        try {
            long docsCount = Long.parseLong(Objects.requireNonNull(
                    elasticsearchAsyncClient.cat().count(c -> c
                                    .index(indexName)
                            ).get()
                            .valueBody()
                            .get(0)
                            .count()));

            return docsCount + 1;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public TypeMapping getTypeMapping() {
        try {
            return elasticsearchAsyncClient
                    .indices()
                    .getMapping(b -> b
                            .index(indexName))
                    .get()
                    .result()
                    .get(indexName)
                    .mappings();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateIndexResponse createIndex(TypeMapping typeMapping) {
        try {
            return elasticsearchAsyncClient.indices().create(c -> c
                    .index(indexName)
                    .mappings(typeMapping)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}

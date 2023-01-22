package com.example.connector;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
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

    public IndexResponse createOrUpdate(Todo todo) {
        try {
            String id = String.valueOf(todo.getId());

            return elasticsearchAsyncClient.index(i -> i
                    .index(indexName)
                    .id(id)
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


    public DeleteByQueryResponse deleteAll() {
        logger.info("deleteAll: ");
        try {
            return elasticsearchAsyncClient.deleteByQuery(d -> d
                    .query(q -> q
                            .matchAll(m -> m
                                    .queryName("title")))
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



    public Todo patch(Long id, Todo newTodo) {

        Todo update = newTodo.patchTodo();

        try {
            elasticsearchAsyncClient.update(u -> u
                            .index(indexName)
                            .id(String.valueOf(id))
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

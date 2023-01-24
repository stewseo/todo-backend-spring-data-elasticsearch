package com.example.connector;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.json.JsonData;
import com.example.model.Todo;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ESClientConnector {

    private static final Logger logger = LoggerFactory.getLogger(ESClientConnector.class);

    private final String indexName = "todo-index";

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    public Todo createOrUpdate(Todo todo) {

        long id = Long.parseLong(elasticsearchAsyncClient.index(i -> i
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
        }).join().id());

        return getById(id);

//        return elasticsearchAsyncClient.get(g -> g.index(indexName), Todo.class).join().source();
    }

    public Todo deleteById(Long id) throws IOException {

        CompletableFuture<DeleteResponse> cf = elasticsearchAsyncClient.delete(d -> d
                .index(indexName)
                .id(String.valueOf(id))
        ).whenComplete((resp, exception) -> {
            if (exception != null) {
                logger.error("Failed to index", exception);
            } else {
                logger.info("Indexed successfully");
            }
        });

        return elasticsearchAsyncClient.get(g -> g
                .id(cf.join().id()
                )
                , Todo.class
        ).join().source();
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
            }).get().source();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Todo> getAll() {

        Query byTimestamp = RangeQuery.of(r -> r
                .field("timestamp")
                .gte(JsonData.of(0)) // get all docs greater than or equal to 0
        )._toQuery();

        return elasticsearchAsyncClient.search(s -> s
                                .index(indexName)
                                .query(q -> q
                                        .bool(b -> b
                                                .must(byTimestamp))
                                )
                                .size(10000)
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

    public Long deleteAll() {

        Query matchAll = MatchAllQuery.of(m -> m
                .queryName("t"))._toQuery();

        return elasticsearchAsyncClient.deleteByQuery(deleteReque -> deleteReque
                        .index("_all")
                        .query(matchAll)
                )
                .join()
                .deleted();

    }

    public Todo patch(Long id,Todo todo) {

        elasticsearchAsyncClient.update(u -> u
                .index(indexName)
                .id(String.valueOf(id))
                .doc(todo),
                Todo.class);

        return todo;

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

    public Todo getMostRecentlyIndexed() {
        return
                elasticsearchAsyncClient.search(c -> c
                                .index(indexName)
                                .sort(z -> z
                                        .field(f -> f
                                                .field("timestamp")
                                                .order(SortOrder.Desc))

                                ).size(1)
                        , Todo.class
                ).join()
                        .hits()
                        .hits()
                        .stream()
                        .map(Hit::source)
                        .findAny().orElse(null);

    }
}

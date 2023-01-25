package com.example.connector;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import com.example.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ESClientConnector {

    private static final Logger logger = LoggerFactory.getLogger(ESClientConnector.class);

    private final String indexName = "todo-index";

    private final SourceConfig sourceConfig = SourceConfig.of(s -> s
            .filter(f -> f
                    .includes(List.of("_id", "todo")))
    );

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    public Todo createOrUpdate(Todo todo) {

        String stringId = todo.getId().toString();

        elasticsearchAsyncClient.index(i -> i
                .index(indexName)
                .id(stringId)
                .pipeline("timestamp-pipeline")
                .document(todo)
        ).whenComplete((resp, exception) -> {
            if (exception != null) {
                logger.error("Failed to index", exception);
            } else {
                logger.info("Indexed successfully");
            }
        }).join();
        return todo;
    }

    public String deleteById(Long id) throws IOException {
        return elasticsearchAsyncClient.delete(d -> d
                .index(indexName)
                .id(String.valueOf(id))
        ).whenComplete((resp, exception) -> {
            if (exception != null) {
                logger.error("Failed to index", exception);
            } else {
                logger.info("Indexed successfully");
            }
        }).join().result().name();
    }

    public Todo getById(Long id) {

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
        }).join().source();

    }

    public List<Todo> getAll() {

        Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();

        return elasticsearchAsyncClient.search(s -> s
                                .index(indexName)
                                .query(matchAllQuery)
                                .size(10000)
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

    public Long deleteAll() {

        Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();

        CompletableFuture<Long> cf = elasticsearchAsyncClient.deleteByQuery(deleteReque -> deleteReque
                .index(indexName)
                .query(matchAllQuery)
        ).whenComplete((resp, excp) -> {
            if (excp != null) {
            } else {
            }

        }).thenApply(DeleteByQueryResponse::deleted);


        return cf.join();
    }

    public Todo patch(Long id, Todo todo) {

        elasticsearchAsyncClient.update(u -> u
                        .index(indexName)
                        .id(String.valueOf(id))
                        .doc(todo),
                Todo.class)
                .join();

        return todo;
    }

    public Long docsCount() {

        return Long.parseLong(
                elasticsearchAsyncClient
                        .cat().count(c -> c
                                .index(indexName)
                        ).join()
                        .valueBody()
                        .get(0)
                        .count()
        );
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

    public DeleteIndexResponse deleteIndex() {

        return elasticsearchAsyncClient.indices().delete(d -> d.index(indexName)).join();
    }
}

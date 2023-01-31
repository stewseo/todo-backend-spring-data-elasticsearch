package com.example.connector;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.cat.CountResponse;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.cat.count.CountRecord;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.exception.RecordNotFoundException;
import com.example.model.Todo;
import com.example.util.QueryBuilderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ESClientConnector {
    @Value("${elastic.index}")
    private String indexName;
    @Value("${elastic.tsPipeline}")
    private String timestampPipeline = "timestamp-pipeline";

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    private ConcurrentHashMap<Long, Todo> todos = new ConcurrentHashMap<>();

    public Long indexDocument(Todo todo) {
        // Create or updateByQuery a document in an index.
        CompletableFuture<String> cf = elasticsearchAsyncClient.index(i -> i
                .index(indexName)
                .id(String.valueOf(todo.getId())
                )
                .pipeline(timestampPipeline)
                .document(todo)
        ).whenComplete((indexResponse, exception) -> {
            if (exception != null) {
                // stub exception
            } else {
            }
        }).thenApply(IndexResponse::id);

        return Long.parseLong(cf.join());
    }

    // https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html
    public Todo createOrUpdate(Todo todo) {
        long id = indexDocument(todo);
        return todos.put(id, todo);
    }

    public Todo getById(Long id) {
        return todos.get(id);
    }

    public List<Todo> getAll() {
        return todos.values().stream().toList();
    }

    public List<Todo> deleteAll() {
        todos.clear();

        CompletableFuture<DeleteByQueryResponse> cf = deleteByQuery(new MatchAllQuery.Builder().build()._toQuery());

        return todos.values().stream().toList();

    }

    public Todo deleteById(Long id) throws IOException {

        Todo t = todos.remove(id);

        CompletableFuture<DeleteByQueryResponse> cf = deleteByQuery(matchQuery("id", id));

        cf.whenComplete((response, exception) -> {
            if (exception != null) {
                throw new RecordNotFoundException("Record Not Found Exception");
            } else {

            }
        });

        return t;
    }

    private CompletableFuture<DeleteByQueryResponse> deleteByQuery(Query query) {
        return elasticsearchAsyncClient.deleteByQuery(deleteReque -> deleteReque
                .index(indexName)
                .query(query)
        );
    }

    public Todo patch(Todo patchWith) throws IOException {
        long id = patchWith.getId();

        todos.replace(id, patchWith);

        updateByQuery(String.valueOf(id), patchWith);

        return todos.get(id);

    }

    private CompletableFuture<String> updateByQuery(String docId, Todo todo) throws IOException {

        return elasticsearchAsyncClient.update(req -> req
                                .index(indexName)
                                .id(docId)
                                .doc(todo)
                        , Todo.class
                ).whenComplete((resp, exception) -> {
                    if (exception != null) {
                        throw new RecordNotFoundException("Record Not Found Exception");
                    } else {
                    }
                }).thenApply(UpdateResponse::id);
    }

    public boolean idExists(Long id) {
        return elasticsearchAsyncClient.exists(e -> e
                .index(indexName)
                .id(id.toString()) // parsing a Long from _id is equal to the corresponding todo's id
                )
                .join().value();
    }

    public Long docsCount() {

        String docsCount = elasticsearchAsyncClient.cat().indices(indicesReq -> indicesReq
                        .index(indexName)
                )
                .thenApply(IndicesResponse::valueBody)
                .join()
                .stream()
                .map(IndicesRecord::docsCount)
                .findAny()
                .orElse("0");

        return Long.parseLong(docsCount);
    }

    private Query matchQuery(String field, Long query) {
        return QueryBuilderUtils.matchQuery(field, String.valueOf(query));
    }


}

package com.example.config;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.MissingRequiredPropertyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ElasticsearchConfigTest {

    @Autowired
    private ElasticsearchAsyncClient elasticsearchAsyncClient;

    @Autowired ElasticsearchConfig elasticsearchConfig;

    @DisplayName("Test Spring @Autowired Integration")
    @Test
    public void testClientInstantiation() throws ExecutionException, InterruptedException {

        assertThat(elasticsearchAsyncClient).isExactlyInstanceOf(ElasticsearchAsyncClient.class);

        MissingRequiredPropertyException ex = assertThrows(MissingRequiredPropertyException.class, () ->
                elasticsearchAsyncClient.exists(e -> e.index("todo-index"))
        );

        assertThat(ex.getPropertyName()).isEqualTo("id");
    }

    @Test
    void testGetPort() {
        assertThat(elasticsearchConfig.getPort()).isEqualTo(443);
    }

    @Test
    void testGetScheme() {
        assertThat(elasticsearchConfig.getScheme()).isEqualTo("https");

    }

    @Test
    void testGetApiKeyId() {
        assertThat(elasticsearchConfig.getApiKeyId()).isEqualTo(System.getenv("API_KEY_ID"));
    }

    @Test
    void testGetApiKeySecret() {
        assertThat(elasticsearchConfig.getApiKeySecret()).isEqualTo(System.getenv("API_KEY_SECRET"));

    }

    @Test
    public void testGetHostName() {
        assertThat(elasticsearchConfig.getHostName())
                .isEqualTo("my-deployment-alias-restaurants-001.es.us-west-1.aws.found.io");

    }

}

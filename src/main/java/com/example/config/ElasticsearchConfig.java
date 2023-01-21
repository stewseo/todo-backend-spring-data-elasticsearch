package com.example.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@ConfigurationProperties("es")
@Getter
@Setter
public class ElasticsearchConfig {

    private String host;
    private int port;
    private String scheme;

    @Bean
    public RestClient createRestClient() {
//        String host = "my-deployment-alias.es.us-west-1.aws.found.io";
//        int port = 443;
//        String scheme = "https";
        String apiKeyId = System.getenv("TODO_API_KEY");
        String apiKeySecret = System.getenv("TODO_API_KEY_PASS");
        String apiKeyIdAndSecret = apiKeyId + ":" + apiKeySecret;

        String encodedApiKey = Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, scheme));

        Header[] defaultHeaders = {new BasicHeader("Authorization", "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        return builder.build();
    }

    @Bean
    public ElasticsearchTransport createTransport() {

        try(RestClient restClient = createRestClient()) {

            final JacksonJsonpMapper mapper = new JacksonJsonpMapper();

            return new RestClientTransport(restClient,mapper);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ElasticsearchAsyncClient getElasticsearchClient(){
        return new ElasticsearchAsyncClient(createTransport());
    }
}


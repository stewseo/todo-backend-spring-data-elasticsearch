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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@ConfigurationProperties("es")
@Getter
@Setter
public class ElasticsearchConfig {
    private String scheme;
    private String apiKeyId;
    private String apiKeySecret;
    private String hostName;
    private int port;

    @Value("my-deployment-alias-restaurants-001.es.us-west-1.aws.found.io")
    private String host;

    @Bean
    public ElasticsearchAsyncClient getEsAsyncClient(){

        String apiKeyIdAndSecret = apiKeyId + ":" + apiKeySecret;

        String encodedApiKey = Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        RestClientBuilder builder = RestClient.builder(new HttpHost(hostName, port, scheme));

        Header[] defaultHeaders = {new BasicHeader("Authorization", "ApiKey " + encodedApiKey)};

        builder.setDefaultHeaders(defaultHeaders);

        RestClient restClient = builder.build();

        final JacksonJsonpMapper mapper = new JacksonJsonpMapper();

        ElasticsearchTransport transport = new RestClientTransport(restClient, mapper);

        return new ElasticsearchAsyncClient(transport);
    }
}


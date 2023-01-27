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
import org.springframework.data.elasticsearch.client.erhlc.RestClients;

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

    @Bean
    public RestClient getESRestClient(){

        String apiKeyIdAndSecret = apiKeyId + ":" + apiKeySecret;

        final String encodedApiKey = Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                .encodeToString((apiKeyIdAndSecret) // Encodes the specified byte array into a String using the Base64 encoding scheme.
                        .getBytes(StandardCharsets.UTF_8));

        RestClientBuilder builder = RestClient.builder(new HttpHost(hostName, port, scheme));

        Header[] defaultHeaders = {new BasicHeader("Authorization", "ApiKey " + encodedApiKey)};

        return builder.setDefaultHeaders(defaultHeaders).build();

    }

    @Bean
    public ElasticsearchAsyncClient getEsAsyncClient(){

        RestClient restClient = getESRestClient();

        JacksonJsonpMapper mapper = new JacksonJsonpMapper();

        ElasticsearchTransport transport = new RestClientTransport(restClient, mapper);

        return new ElasticsearchAsyncClient(transport);
    }
}


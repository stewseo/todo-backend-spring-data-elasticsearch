package com.example.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties("timestamp")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(indexName = "todo-index")
@Data
public class Todo {
    private String title;
    @Id
    private Long id;
    private String url;
    private Integer order;
    private Boolean completed;

    public Todo(String title, String url, Long id) {
        this.title = title;
        this.url = url;
        this.id = id;
    }

    public Todo(String title, String url) {
        this.title = title;
        this.url = url;
    }
}

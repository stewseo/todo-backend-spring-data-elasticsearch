package com.example.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@JsonIgnoreProperties("timestamp")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(indexName = "todo-index")
@Data
public class Todo {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "url")

    private String url;

    @Field(type = FieldType.Boolean, name = "completed")
    @Builder.Default
    private Boolean completed = false;

    @Field(type = FieldType.Integer, name = "order")
    private Integer order;

    public Todo patchTodo() {

        Todo.TodoBuilder update = new Todo.TodoBuilder();

        if (this.title != null) {
            update.title(this.getTitle());
        }

        if (this.completed) {
            update.completed(true);
        }

        if (this.order != null) {
            update.order(this.order);
        }
        return update.build();
    }
}

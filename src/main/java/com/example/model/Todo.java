package com.example.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
    private Boolean completed;

    @Field(type = FieldType.Integer, name = "order")
    private Integer order;

    public Todo patchTodo() {

        Todo.TodoBuilder update = new Todo.TodoBuilder();

        if (this.title != null) {
            update.title(this.getTitle());
        }

//        if (this.completed) {
//            update.completed(this.completed);
//        }

        if (this.order != null) {
            update.order(this.order);
        }
        return update.build();
    }
}

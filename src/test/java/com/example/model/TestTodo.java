package com.example.model;

public class TestTodo {

    public static String DEFAULT_TITLE = "a todo";

    public static Todo generateInvalidTodo() {
        return new Todo.TodoBuilder().title(DEFAULT_TITLE).build();
    }

    public static Todo generateValidTodo() {
        return new Todo.TodoBuilder()
                .title(DEFAULT_TITLE)
                .id(1L)
                .url("https://url/todos/" + 1L)
                .build();
    }

    public static Todo generatePatchWithTodo() {
        return new Todo.TodoBuilder()
                .title("patched todo")
                .build();
    }
}

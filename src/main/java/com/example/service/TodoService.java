package com.example.service;

import java.io.IOException;
import java.util.List;

public interface TodoService<T, Long> {

    Object createOrUpdate(T t) throws IOException;

    T getById(Long id);

    List<T> getAll() throws IOException;

    Object deleteAll();

    Object deleteById(Long id) throws IOException;

    T patch(Long id, T t);
}

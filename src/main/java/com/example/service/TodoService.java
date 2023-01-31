package com.example.service;

import com.example.exception.RecordNotFoundException;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.IOException;
import java.util.List;

public interface TodoService<T, Long> {

    Object createOrUpdate(T t) throws RecordNotFoundException, IOException;

    T getById(Long id) throws IOException;

    List<T> getAll() throws IOException;

    Object deleteAll() throws IOException;

    Object deleteById(Long id) throws IOException;

    T patch(Long id, T t) throws RecordNotFoundException, IOException;

}

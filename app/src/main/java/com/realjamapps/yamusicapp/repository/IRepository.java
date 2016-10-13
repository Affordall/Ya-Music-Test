package com.realjamapps.yamusicapp.repository;

import com.realjamapps.yamusicapp.specifications.ISpecification;

import java.util.List;

public interface IRepository<T> {
    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    void remove(ISpecification specification);

    boolean isItemExist(String name);

    List<T> query(ISpecification specification);
}

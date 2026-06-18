package com.rosterforge.dao;

import java.util.List;

public interface IDao<T> {
    void save(T obj);
    T findById(long id);
    List<T> findAll();
    void delete(long id);
}

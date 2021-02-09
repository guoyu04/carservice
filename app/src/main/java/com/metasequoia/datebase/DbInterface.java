package com.metasequoia.datebase;

import java.util.Collection;
import java.util.List;

public interface DbInterface <T>{
    long save(T obj);
    long saveAll(Collection<T> collection);
    List<T> queryAll(T table);
    List<T> queryAll(String order);
    T queryById(Class<T> table, Object id);
    int delete(Class table, int id);
}

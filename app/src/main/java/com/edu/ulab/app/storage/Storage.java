package com.edu.ulab.app.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Storage <T> {
    private ConcurrentHashMap <Long, T> dataBase = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1);

    public long setEntity (T entity) {
        dataBase.put(currentId.get(), entity);
        return currentId.getAndIncrement();
    }

    public long setEntity (Long id, T entity) {
        dataBase.put(id, entity);
        return id;
    }

    public List<T> getAllValues() {
        return new ArrayList<>(dataBase.values());
    }

    public ConcurrentHashMap<Long, T> getAll() {
        return dataBase;
    }

    public T getEntity (Long id) {
        return dataBase.get(id);
    }

    public void deleteEntity(Long id) {
        dataBase.remove(id);
        currentId.decrementAndGet();
    }


    //todo
    // продумать возможные ошибки
    // продумать что у узера может быть много книг и нужно создать эту связь
}

package com.edu.ulab.app.storage;
import com.edu.ulab.app.exception.DuplicatedException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Repository<T> {

    Storage<T> storage = new Storage<>();

    public Long create(T entity) {
        Optional dublicate = storage.getAllValues()
                .stream()
                .filter(entity::equals)
                .findFirst();
        if (dublicate.isPresent()) {
            throw new DuplicatedException("User or book already exist. Cannot add: "
                    + dublicate.get().getClass().getSimpleName());
        } else return storage.setEntity(entity);
    }

    public ConcurrentHashMap<Long, T> getAll() {
        return storage.getAll();
    }

    public long setEntity (Long id, T entity) {
        return storage.setEntity(id, entity);
    }

    public T getEntity(Long id) {
        T entity = storage.getEntity(id);
        return (entity != null) ? storage.getEntity(id) : null;
    }

    public List<T> getAllValues (){
        return storage.getAllValues();
    }


    public void deleteEntity(Long id) {
        storage.deleteEntity(id);
    }

}

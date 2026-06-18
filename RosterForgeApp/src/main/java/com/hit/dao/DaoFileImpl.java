package com.hit.dao;

import java.io.*;
import java.util.*;

public class DaoFileImpl<T extends Serializable> implements IDao<T> {

    private final String filePath;
    private final IdExtractor<T> idExtractor;

    @FunctionalInterface
    public interface IdExtractor<T> {
        long getId(T obj);
    }

    public DaoFileImpl(String filePath, IdExtractor<T> idExtractor) {
        this.filePath = filePath;
        this.idExtractor = idExtractor;
    }

    @Override
    public synchronized void save(T obj) {
        Map<Long, T> store = readAll();
        store.put(idExtractor.getId(obj), obj);
        writeAll(store);
    }

    @Override
    public synchronized T findById(long id) {
        return readAll().get(id);
    }

    @Override
    public synchronized List<T> findAll() {
        return new ArrayList<>(readAll().values());
    }

    @Override
    public synchronized void delete(long id) {
        Map<Long, T> store = readAll();
        store.remove(id);
        writeAll(store);
    }

    @SuppressWarnings("unchecked")
    private Map<Long, T> readAll() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) return new LinkedHashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<Long, T>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LinkedHashMap<>();
        }
    }

    private void writeAll(Map<Long, T> store) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(store);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to " + filePath, e);
        }
    }
}

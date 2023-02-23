package com.ps.kidsworld.utils.lrucache;

public interface Cache<K, V> {
    boolean put(K key, V value);

    V get(K key);

    int size();

    boolean isEmpty();

    void clear();
}
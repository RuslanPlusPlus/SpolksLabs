package com.rusned.spolks.pool;


public interface Connection {
    void create(Integer port, Integer backlog);
    void listen();
}

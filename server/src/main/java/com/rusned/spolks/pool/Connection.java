package com.rusned.spolks.pool;


import java.io.IOException;

public interface Connection {
    void create(Integer port, Integer backlog);
    void listen() throws IOException;
    void write(String data);
}

package com.rusned.spolks.pool;


import java.io.IOException;

public interface Connection {
    void create(Integer port, Integer backlog);
    void listen() throws IOException;
    void write(String data);
    void write(byte[] bytes, int length) throws IOException;
    String read() throws IOException;
    int read (byte[] buffer, int offset, int length) throws IOException;
}

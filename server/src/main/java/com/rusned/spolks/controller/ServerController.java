package com.rusned.spolks.controller;

import com.rusned.spolks.pool.Connection;

public interface ServerController {
    Connection getConnection();
    void runServer();
}

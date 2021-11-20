package com.rusned.spolks.controller.impl;

import com.rusned.spolks.pool.Connection;
import com.rusned.spolks.controller.ServerController;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TcpServerController implements ServerController {
    private static TcpServerController instance;
    private static final AtomicBoolean isCreated = new AtomicBoolean(false);
    private static final Lock lock = new ReentrantLock(true);

    private TcpServerController(){}

    public static TcpServerController getInstance() {
        if (!isCreated.get()){
            lock.lock();
            if (instance == null){
                instance = new TcpServerController();
                isCreated.set(true);
            }
            lock.unlock();
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void runServer() {

    }
}

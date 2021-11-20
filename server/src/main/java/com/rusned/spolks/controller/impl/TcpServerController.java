package com.rusned.spolks.controller.impl;

import com.rusned.spolks.pool.Connection;
import com.rusned.spolks.controller.ServerController;
import com.rusned.spolks.pool.impl.TcpConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TcpServerController implements ServerController {
    private static final String PROPERTIES_PATH = "configuration.properties";
    private static Integer port;
    private static Integer backlog;
    private static final Properties properties = new Properties();
    private static TcpServerController instance;
    private static final AtomicBoolean isCreated = new AtomicBoolean(false);
    private static final Lock lock = new ReentrantLock(true);

    private TcpServerController(){
        ClassLoader classLoader = TcpServerController.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(PROPERTIES_PATH);
        try {
            properties.load(inputStream);
            port = Integer.parseInt(properties.getProperty("server.port"));
            backlog = Integer.parseInt(properties.getProperty("server.backlog"));
            log.info("Server port set to: " + port);
            log.info("Server backlog set to: " + backlog);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to read properties file!!!", e);
        }
    }

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
        return TcpConnection.getInstance();
    }

    @Override
    public void runServer() {
        Connection connection = TcpConnection.getInstance();
        connection.create(port, backlog);
        while (true) {
            try {
                connection.listen();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

}

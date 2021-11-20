package com.rusned.spolks.pool.impl;

import com.rusned.spolks.command.factory.CommandFactory;
import com.rusned.spolks.controller.impl.TcpServerController;
import com.rusned.spolks.pool.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.net.SocketOptions.SO_TIMEOUT;

@Slf4j
public class TcpConnection implements Connection {

    private static int PORT;
    private static int BACKLOG;
    private static final int BUFFER_SIZE = 256;

    private ServerSocket serverSocket;
    private final CommandFactory commandFactory;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] clientMessage;

    private static TcpConnection instance;
    private static final AtomicBoolean isCreated = new AtomicBoolean(false);
    private static final Lock lock = new ReentrantLock(true);

    private TcpConnection(){
        commandFactory = new CommandFactory();
        clientMessage = new byte[BUFFER_SIZE];
    }

    public static TcpConnection getInstance() {
        if (!isCreated.get()){
            lock.lock();
            if (instance == null){
                instance = new TcpConnection();
                isCreated.set(true);
            }
            lock.unlock();
        }
        return instance;
    }

    @Override
    public void create(Integer port, Integer backlog) {
        try {
            log.info("Server is staring...");
            PORT = port;
            BACKLOG = backlog;
            serverSocket = new ServerSocket(PORT, BACKLOG);
            log.info("Server is started at {}:{}",
                    InetAddress.getLocalHost().getHostAddress(),
                    PORT);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void listen() throws IOException {
        log.info("Server is listening...");
        Socket clientSocket = serverSocket.accept();
        //client.setSoTimeout(SO_TIMEOUT);
        initStream(clientSocket);
        if (clientSocket.isConnected()) {
            log.info("Client {} was connected", clientSocket.getRemoteSocketAddress());
        }
        while (clientSocket.isConnected()){
            int countOfBytes;
            /*
            if ((countOfBytes = inputStream.read(clientMessage)) == -1) {
                break;
            }
            String clientInput = new String(clientMessage, 0, countOfBytes);

             */
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientInput = input.readLine();
            if (clientInput == null){
                break;
            }
            // TODO: 21.11.2021 complete 
            log.info("Client input: {}", clientInput);
        }
        closeClientConnection(clientSocket);
    }

    private void initStream(Socket clientSocket) throws IOException {
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
    }

    private void closeClientConnection(Socket clientSocket) throws IOException {
        inputStream.close();
        outputStream.close();
        clientSocket.close();
        log.info("Client {} was disconnected", clientSocket.getRemoteSocketAddress());
    }
}

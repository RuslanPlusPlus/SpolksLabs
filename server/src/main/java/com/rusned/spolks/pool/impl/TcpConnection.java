package com.rusned.spolks.pool.impl;

import com.rusned.spolks.command.Command;
import com.rusned.spolks.command.factory.CommandFactory;
import com.rusned.spolks.exception.CommandNotFoundException;
import com.rusned.spolks.exception.InvalidCommandFormatException;
import com.rusned.spolks.pool.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TcpConnection implements Connection {

    private static final int BUFFER_SIZE = 256;
    private static final String CLOSE_COMMAND = "close";

    private ServerSocket serverSocket;
    private final CommandFactory commandFactory;
    private InputStream clientByteInputStream;
    //private OutputStream outputStream;
    private PrintWriter clientOutputStream;
    private BufferedReader clientInputStream;
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
            serverSocket = new ServerSocket(port, backlog);
            log.info("Server is started at {}:{}",
                    InetAddress.getLocalHost().getHostAddress(),
                    port);
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
            /*
            int countOfBytes;
            if ((countOfBytes = inputStream.read(clientMessage)) == -1) {
                break;
            }
            String clientInput = new String(clientMessage, 0, countOfBytes);
             */
            clientOutputStream.println("Server is waiting for command");
            try {
                String clientInput = clientInputStream.readLine();
                if (clientInput == null){
                    break;
                }
                if (clientInput.equals(CLOSE_COMMAND) || clientInput.equals(CLOSE_COMMAND.toUpperCase())){
                    break;
                }
                log.info("Client input: {}", clientInput);
                Command clientCommand = commandFactory.defineCommand(clientInput);
                clientCommand.execute();
            } catch (InvalidCommandFormatException | CommandNotFoundException e) {
                log.error(e.getMessage());
                //break;
            }
        }
        closeClientConnection(clientSocket);
    }

    @Override
    public void write(String data) {
        clientOutputStream.println(data);
    }

    @Override
    public String read() throws IOException {
        return clientInputStream.readLine();
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return clientByteInputStream.read(buffer, offset, length);
    }

    private void initStream(Socket clientSocket) throws IOException {
        clientInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        clientOutputStream = new PrintWriter(clientSocket.getOutputStream(), true);
        clientByteInputStream = clientSocket.getInputStream();
        //outputStream = clientSocket.getOutputStream();
    }

    private void closeClientConnection(Socket clientSocket) throws IOException {
        clientByteInputStream.close();
        //outputStream.close();
        clientInputStream.close();
        clientOutputStream.close();
        clientSocket.close();
        log.info("Client {} was disconnected", clientSocket.getRemoteSocketAddress());
    }
}

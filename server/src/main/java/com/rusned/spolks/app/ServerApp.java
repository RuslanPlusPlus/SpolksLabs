package com.rusned.spolks.app;

import com.rusned.spolks.controller.impl.TcpServerController;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    //private static final String EXIT_COMMAND = "exit";

    public static void main(String[] args) {
        /*
        final int port = 85;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println(
                    "Server opened on address: "
                    + inetAddress.getHostAddress()
                    + ":"
                    + serverSocket.getLocalPort()
            );


            Socket clientSocket = serverSocket.accept();
            System.out.println(
                    "Client with "
                            + clientSocket.getInetAddress() + ":"
                            + clientSocket.getPort()
                            + " connected"
            );

            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;
            do {
                output.println("Hello client!!!");
                message = input.readLine();
                System.out.println(message);
            }while (message != null && !message.equals(EXIT_COMMAND));

        } catch (IOException e) {
            e.printStackTrace();
        }

         */
        TcpServerController.getInstance().runServer();
    }
}

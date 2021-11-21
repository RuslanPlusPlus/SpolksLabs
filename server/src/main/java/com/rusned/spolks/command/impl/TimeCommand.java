package com.rusned.spolks.command.impl;

import com.rusned.spolks.controller.ServerController;
import com.rusned.spolks.controller.impl.TcpServerController;
import com.rusned.spolks.pool.Connection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeCommand extends BaseCommand {

    private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final ServerController serverController;

    public TimeCommand(){
        serverController = TcpServerController.getInstance();
    }

    @Override
    public void execute() {
        Connection tcpConnection = serverController.getConnection();
        if (tcpConnection != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
            tcpConnection.write(currentTime.format(dateTimeFormatter));
        }
    }
}

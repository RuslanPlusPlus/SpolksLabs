package com.rusned.spolks.command.impl;

import com.rusned.spolks.controller.ServerController;
import com.rusned.spolks.controller.impl.TcpServerController;
import com.rusned.spolks.pool.Connection;
import com.rusned.spolks.util.ValidToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoCommand extends BaseCommand {

    private final ServerController serverController;

    public EchoCommand(){
        serverController = TcpServerController.getInstance();
    }

    @Override
    public void execute() {
        String content = getAllTokens().get(ValidToken.CONTENT.getName());
        if (content != null) {
            log.info("Client output(echo command): {}", content);
            Connection tcpConnection = serverController.getConnection();
            tcpConnection.write(content);
        }
    }
}

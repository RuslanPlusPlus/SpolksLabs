package com.rusned.spolks.command.impl;

import com.rusned.spolks.controller.ServerController;
import com.rusned.spolks.controller.impl.TcpServerController;
import com.rusned.spolks.pool.Connection;
import com.rusned.spolks.util.TokenGenerator;
import com.rusned.spolks.util.ValidToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UploadCommand extends BaseCommand{

    private final ServerController serverController;
    private static final String SPACE_REGEX = " ";
    private static final String CONFIRMATION_POSITIVE = "yes";
    private static final String CONFIRMATION_NEGATIVE = "no";
    private static final String START_TRANSFER = "transfer_start";
    private static final int BUFF_SIZE = 500_000;
    private final Set<String> clientTokens;

    public UploadCommand(){
        serverController = TcpServerController.getInstance();
        clientTokens = new HashSet<>();
    }

    @Override
    public void execute() throws IOException {
        Connection tcpConnection = serverController.getConnection();
        if (tcpConnection != null) {
            String clientToken = getAllTokens().get(ValidToken.SESSION_KEY.getName());
            if (clientToken != null) {
                log.info("Client token {}", clientToken);
            }
            if (clientToken != null && clientTokens.contains(clientToken)) {
                log.info("Have old token {}", clientToken);
            } else {
                log.warn("No client token");
                String token = TokenGenerator.generate();
                log.info("Generate new token {}", token);
                clientTokens.add(token);
            }
            tcpConnection.write("Do you really want to upload this file? [yes/no]");
            String[] confirmParams = tcpConnection.read().split(SPACE_REGEX);
            File file = new File(getAllTokens().get(ValidToken.NAME.getName()));
            final long fileSize = Long.parseLong(confirmParams[1]);
            final String confirmation = confirmParams[0];
            log.info("Confirmation - {}", confirmation);
            if (confirmation.equals(CONFIRMATION_POSITIVE)){
                long receivedBytes = 0;
                long position = 0;
                if (file.exists()) {
                    position = file.length();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.getChannel().position(position);
                tcpConnection.write(START_TRANSFER + " from " + position);
                log.info("Starting from byte position: " + position);
                final long startTime = System.currentTimeMillis();
                int count;
                byte[] fileBuffer = new byte[BUFF_SIZE];
                int bufferPosition = 0;
                while ((count = tcpConnection.read(fileBuffer, bufferPosition, BUFF_SIZE - bufferPosition)) != -1) {
                    receivedBytes += count;
                    bufferPosition += count;
                    if (bufferPosition == BUFF_SIZE) {
                        fileOutputStream.write(Arrays.copyOfRange(fileBuffer, 0, BUFF_SIZE));
                        bufferPosition = 0;
                    }
                    log.info("Current received bytes: {}", receivedBytes);
                    if (receivedBytes >= fileSize) {
                        break;
                    }

                }
                if (bufferPosition != 0) {
                    fileOutputStream.write(Arrays.copyOfRange(fileBuffer, 0, bufferPosition));
                }
                fileOutputStream.close();
                log.info((int) (((double) receivedBytes / fileSize) * 100) + "%");

                long resultTimeInSeconds = TimeUnit.SECONDS.convert(
                        System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
                log.info("File is downloaded. Total size: {} bytes", receivedBytes);
                log.info(String.format("Total time: %d s", resultTimeInSeconds));
                log.info(String.format("Bandwidth: %.3f B/s",
                        (((double) receivedBytes) / resultTimeInSeconds)));

            }
        }
    }
}

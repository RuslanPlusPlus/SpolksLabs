package com.rusned.spolks.command.impl;

import com.rusned.spolks.controller.ServerController;
import com.rusned.spolks.controller.impl.TcpServerController;
import com.rusned.spolks.pool.Connection;
import com.rusned.spolks.util.TokenGenerator;
import com.rusned.spolks.util.ValidToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DownloadCommand extends BaseCommand {

    private final ServerController serverController;
    private final Map<String, Long> filesTokens;
    //private final Set<String> clientTokens;
    private static final String CONFIRMATION_POSITIVE = "yes";
    //private static final String CONFIRMATION_NEGATIVE = "no";
    private static final String START_TRANSFER = "transfer_start";
    private static final int BUFF_SIZE = 500_000;
    private static final String SPACE_REGEX = " ";

    public DownloadCommand(){
        serverController = TcpServerController.getInstance();
        //clientTokens = new HashSet<>();
        filesTokens = new HashMap<>();
    }

    @Override
    public void execute() {
        try {
            String filePath = getAllTokens().get(ValidToken.PATH.getName());
            if (filePath != null) {
                executeCommand(filePath);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private void executeCommand(String filePath) throws IOException, InterruptedException {
        Connection tcpConnection = serverController.getConnection();
        if (tcpConnection != null) {
            String clientToken = getAllTokens().get(ValidToken.SESSION_KEY.getName());
            if (clientToken != null) {
                log.info("Client token {}", clientToken);
            }

            File file = new File(filePath);
            final long fileSize = file.length();
            if (file.exists() && !file.isDirectory()){
                String token;
                if (clientToken != null && filesTokens.containsKey(clientToken)) {
                    log.info("Have old token {}", clientToken);
                    token = clientToken;
                } else {
                    log.warn("No client token");
                    token = TokenGenerator.generate();
                    log.info("Generate new token {}", token);
                    filesTokens.put(token, 0L);
                    tcpConnection.write("SUCCESS "
                            + "File size: " + fileSize
                            + " "
                            + "Client token: " + TokenGenerator.generate()
                    );
                }
                tcpConnection.write("Do you really want to download this file? [yes/no bytes_to_skip]");
                String startMessage = tcpConnection.read();
                String[] confirmParams = startMessage.split(SPACE_REGEX);
                long bytesToSkip = Integer.parseInt(confirmParams[1]);
                final String confirmation = confirmParams[0];
                if (confirmation.equals(CONFIRMATION_POSITIVE)) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    log.info("Bytes to skip {}", bytesToSkip);
                    long skippedBytes = fileInputStream.skip(bytesToSkip);
                    if (skippedBytes != bytesToSkip) {
                        log.warn("Expected skipped bytes={} and actual skipped bytes={}", bytesToSkip, skippedBytes);
                    }
                    byte[] fileContent = new byte[BUFF_SIZE];
                    //Date start = new Date();
                    final long startTime = System.currentTimeMillis();
                    int receivedBytes;
                    while ((receivedBytes = fileInputStream.read(fileContent, 0, BUFF_SIZE)) != -1) {
                        tcpConnection.write(fileContent, receivedBytes);
                        bytesToSkip += receivedBytes;
                        filesTokens.put(token, bytesToSkip);
                        //Thread.sleep(1);
                    }
                    filesTokens.remove(token);
                    log.info("File is transferred");
                    tcpConnection.write("\n");

                    //Date end = new Date();
                    //long resultTime = end.getTime() - start.getTime();
                    //long resultMilliSeconds = System.currentTimeMillis() - startTime;
                    long resultNanoSeconds = TimeUnit.NANOSECONDS.convert(
                            System.currentTimeMillis() - startTime,
                                        TimeUnit.MILLISECONDS);
                    //long resultTimeInSeconds = TimeUnit.SECONDS.convert(resultTime, TimeUnit.MILLISECONDS);
                    //log.info("Transfer time: {}", ((resultTimeInSeconds > 0) ? resultTimeInSeconds + "s" : resultTime + "ms"));
                    log.info(String.format("Transfer time: %.3f ns", (double)resultNanoSeconds));
                    log.info(String.format("Bandwidth: %.3f B/ns",
                            (((double) bytesToSkip) / resultNanoSeconds)));
                } else {
                    log.info("{} flag not founded...", START_TRANSFER);
                }
            }else{
                String message = "File does not exists or something went wrong";
                tcpConnection.write(message);
                log.warn(message);
            }
        }
    }
}

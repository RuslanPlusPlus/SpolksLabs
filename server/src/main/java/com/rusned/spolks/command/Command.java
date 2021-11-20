package com.rusned.spolks.command;

import com.rusned.spolks.exception.InvalidCommandFormatException;

import java.util.Map;

public interface Command {
    void execute();
    void putToken(String name, String value);
    void verifyTokens() throws InvalidCommandFormatException;
    Map<String, String> getAllTokens();
    Map<String, String> getValidTokens();
}

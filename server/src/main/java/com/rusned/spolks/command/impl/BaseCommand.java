package com.rusned.spolks.command.impl;

import com.rusned.spolks.command.Command;
import com.rusned.spolks.exception.InvalidCommandFormatException;
import com.rusned.spolks.util.ValidToken;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand implements Command {
    private final Map<String, String> validTokens;
    private final Map<String, String> tokens;

    BaseCommand(){
        validTokens = new HashMap<>();
        Arrays
                .stream(ValidToken.values())
                .forEach(t -> validTokens.put(t.getName(), t.getRegex()));
        tokens = new HashMap<>();
    }

    @Override
    public Map<String, String> getValidTokens() {
        return validTokens;
    }

    @Override
    public Map<String, String> getAllTokens() {
        return tokens;
    }

    @Override
    public void verifyTokens() throws InvalidCommandFormatException {
        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> entry : tokens.entrySet()) {
                final String key = entry.getKey();

                if (!validTokens.containsKey(key)) {
                    throw new InvalidCommandFormatException("The command does not contain '" + key + "' token.");
                }
            }
        }
    }

    @Override
    public void putToken(String name, String value) {
        this.tokens.put(name, value);
    }
}

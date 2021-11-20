package com.rusned.spolks.command.factory;

import com.rusned.spolks.command.Command;
import com.rusned.spolks.command.CommandType;
import com.rusned.spolks.exception.CommandNotFoundException;
import com.rusned.spolks.exception.InvalidCommandFormatException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommandFactory {

    private static final String INPUT_REGEX = "^([a-z]+)( -[a-z_]+((?==)='[\\w .-:\\\\]+')*)*$";
    private static final String TOKEN_REGEX = "(-([a-z_]+)((?==)='([\\w .-:\\\\]+)')*)";
    private static final int COMMAND_GROUP_INDEX = 1;
    private static final int TOKEN_NAME_GROUP_INDEX = 2;
    private static final int TOKEN_VALUE_GROUP_INDEX = 4;

    private Command command;

    public Command defineCommand(String input) throws InvalidCommandFormatException, CommandNotFoundException {
        Pattern pattern = Pattern.compile(INPUT_REGEX);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            throw new InvalidCommandFormatException("Wrong command format!");
        }

        final String commandName = matcher.group(COMMAND_GROUP_INDEX);

        if (!CommandType.hasCommand(commandName)) {
            throw new CommandNotFoundException("Wrong command: " + commandName);
        }

        CommandType commandType = CommandType.valueOf(commandName.toUpperCase());
        command = commandType.getCommand();
        parseTokens(input);
        log.info("Command created: " + command.getClass());
        return command;
    }

    private void parseTokens(String input) throws InvalidCommandFormatException {
        Pattern pattern = Pattern.compile(TOKEN_REGEX);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            final String tokenName = matcher.group(TOKEN_NAME_GROUP_INDEX);
            final String tokenValue = matcher.group(TOKEN_VALUE_GROUP_INDEX);
            command.putToken(tokenName, tokenValue);
        }
        command.verifyTokens();
    }
}

package com.rusned.spolks.command;

import com.rusned.spolks.command.impl.DownloadCommand;
import com.rusned.spolks.command.impl.EchoCommand;
import com.rusned.spolks.command.impl.TimeCommand;
import com.rusned.spolks.command.impl.UploadCommand;

public enum CommandType {
    ECHO(new EchoCommand()),
    DOWNLOAD(new DownloadCommand()),
    UPLOAD(new UploadCommand()),
    TIME(new TimeCommand());

    private final Command command;

    CommandType(Command command){
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public static boolean hasCommand(String commandName){
        boolean result = false;
        for(CommandType commandType: CommandType.values()){
            try{
                if (CommandType.valueOf(commandName.toUpperCase()).equals(commandType)){
                    result = true;
                }
            }catch (IllegalArgumentException e){
                result = false;
            }
        }
        return result;
    }
}

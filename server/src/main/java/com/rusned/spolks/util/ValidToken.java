package com.rusned.spolks.util;

public enum ValidToken {
    PATH("path", "^[\\w .-:\\\\]+$"),
    NAME("name", "^[\\w .-:\\\\]+$"),
    SESSION_KEY("session_key", "^[\\w .-:\\\\]+$"),
    CONTENT("content", null);

    private final String name;
    private final String regex;

    ValidToken(String name, String regex) {
        this.name = name;
        this.regex = regex;
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }
}

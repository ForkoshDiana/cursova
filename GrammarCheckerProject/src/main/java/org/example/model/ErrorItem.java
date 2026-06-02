package org.example.model;

public class ErrorItem {
    private final ErrorType type;
    private final int start;
    private final int end;
    private final String fragment;
    private final String message;
    private final String suggestion;
    private final boolean safeAutoFix;

    public ErrorItem(ErrorType type, int start, int end, String fragment,
                     String message, String suggestion, boolean safeAutoFix) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.fragment = fragment;
        this.message = message;
        this.suggestion = suggestion;
        this.safeAutoFix = safeAutoFix;
    }

    public ErrorType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getFragment() {
        return fragment;
    }

    public String getMessage() {
        return message;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public boolean isSafeAutoFix() {
        return safeAutoFix;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + fragment + " -> " + message +
                (suggestion == null || suggestion.isBlank() ? "" : " | Порада: " + suggestion);
    }
}
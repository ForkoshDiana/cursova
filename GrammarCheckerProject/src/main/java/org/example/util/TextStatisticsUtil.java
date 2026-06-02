package org.example.util;

import org.example.model.TextStatistics;

public class TextStatisticsUtil {

    public static TextStatistics calculate(String text) {
        if (text == null) {
            text = "";
        }

        int characters = text.length();
        int charactersNoSpaces = text.replaceAll("\\s+", "").length();

        String trimmed = text.trim();
        int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

        int sentences = 0;
        for (char c : text.toCharArray()) {
            if (c == '.' || c == '!' || c == '?') {
                sentences++;
            }
        }

        int paragraphs = trimmed.isEmpty() ? 0 : text.split("(\\r?\\n){2,}|\\r?\\n").length;

        return new TextStatistics(characters, charactersNoSpaces, words, sentences, paragraphs);
    }
}
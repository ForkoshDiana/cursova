package org.example.model;

public class TextStatistics {
    private final int characters;
    private final int charactersNoSpaces;
    private final int words;
    private final int sentences;
    private final int paragraphs;

    public TextStatistics(int characters, int charactersNoSpaces, int words, int sentences, int paragraphs) {
        this.characters = characters;
        this.charactersNoSpaces = charactersNoSpaces;
        this.words = words;
        this.sentences = sentences;
        this.paragraphs = paragraphs;
    }

    public int getCharacters() {
        return characters;
    }

    public int getCharactersNoSpaces() {
        return charactersNoSpaces;
    }

    public int getWords() {
        return words;
    }

    public int getSentences() {
        return sentences;
    }

    public int getParagraphs() {
        return paragraphs;
    }
}
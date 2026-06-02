package org.example.service;

import org.example.model.ErrorItem;

import java.util.List;

public interface GrammarChecker {
    List<ErrorItem> check(String text);
}
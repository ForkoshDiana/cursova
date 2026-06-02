package org.example.service;

import org.example.model.ErrorItem;
import org.example.model.ErrorType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UkrainianGrammarChecker implements GrammarChecker {

    private static final Map<Pattern, RuleInfo> FIXED_RULES = new LinkedHashMap<>();

    static {
        FIXED_RULES.put(Pattern.compile("\\bне має\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.GRAMMAR, "Неправильна форма", "немає", true));

        FIXED_RULES.put(Pattern.compile("\\bна протязі\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.STYLE, "Невдалий мовний зворот", "протягом", true));

        FIXED_RULES.put(Pattern.compile("\\bпо українськи\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.GRAMMAR, "Слід використовувати дефіс", "по-українськи", true));

        FIXED_RULES.put(Pattern.compile("\\bбудь ласка\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.PUNCTUATION, "У багатьох випадках нормативно: через дефіс", "будь-ласка", true));

        FIXED_RULES.put(Pattern.compile("\\bвірно\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.STYLE, "У значенні \"правильно\" краще вживати \"правильно\"", "правильно", true));

        FIXED_RULES.put(Pattern.compile("\\bжи\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.TYPO, "Після \"ж\" вживається \"и\", а не \"і\"-форма тут підозріла", null, false));

        FIXED_RULES.put(Pattern.compile("\\bши\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
                new RuleInfo(ErrorType.TYPO, "Після \"ш\" вживається \"и\", а не \"і\"-форма тут підозріла", null, false));
    }

    @Override
    public List<ErrorItem> check(String text) {
        List<ErrorItem> errors = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return errors;
        }

        checkDoubleSpaces(text, errors);
        checkSpaceBeforePunctuation(text, errors);
        checkMissingSpaceAfterPunctuation(text, errors);
        checkRepeatedWords(text, errors);
        checkSentenceCapitalization(text, errors);
        checkSentenceEndings(text, errors);
        checkMultiplePunctuation(text, errors);
        checkLatinFragments(text, errors);
        checkCommonRules(text, errors);

        errors.sort(Comparator.comparingInt(ErrorItem::getStart));
        return errors;
    }

    private void checkDoubleSpaces(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile(" {2,}").matcher(text);
        while (m.find()) {
            errors.add(new ErrorItem(
                    ErrorType.SPACING,
                    m.start(),
                    m.end(),
                    text.substring(m.start(), m.end()),
                    "Знайдено кілька пробілів підряд",
                    "Замінити на один пробіл",
                    true
            ));
        }
    }

    private void checkSpaceBeforePunctuation(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile("\\s+[,.!?;:]").matcher(text);
        while (m.find()) {
            errors.add(new ErrorItem(
                    ErrorType.PUNCTUATION,
                    m.start(),
                    m.end(),
                    text.substring(m.start(), m.end()),
                    "Не повинно бути пробілу перед розділовим знаком",
                    text.substring(m.start(), m.end()).trim(),
                    true
            ));
        }
    }

    private void checkMissingSpaceAfterPunctuation(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile("[,.!?;:][^\\s\\n\\r\\d»”)]").matcher(text);
        while (m.find()) {
            String fragment = text.substring(m.start(), Math.min(m.end(), text.length()));
            errors.add(new ErrorItem(
                    ErrorType.PUNCTUATION,
                    m.start(),
                    m.end(),
                    fragment,
                    "Після розділового знака зазвичай потрібен пробіл",
                    fragment.charAt(0) + " " + fragment.substring(1),
                    true
            ));
        }
    }

    private void checkRepeatedWords(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile("\\b([\\p{L}’'-]+)\\s+\\1\\b",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(text);
        while (m.find()) {
            String repeated = m.group();
            String word = m.group(1);
            errors.add(new ErrorItem(
                    ErrorType.STYLE,
                    m.start(),
                    m.end(),
                    repeated,
                    "Повтор слова поспіль",
                    word,
                    true
            ));
        }
    }

    private void checkSentenceCapitalization(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile("(^|[.!?]\\s+)([а-яіїєґ])",
                Pattern.UNICODE_CASE).matcher(text);
        while (m.find()) {
            int pos = m.start(2);
            String ch = m.group(2);
            errors.add(new ErrorItem(
                    ErrorType.CAPITALIZATION,
                    pos,
                    pos + 1,
                    ch,
                    "Речення бажано починати з великої літери",
                    ch.toUpperCase(),
                    true
            ));
        }
    }

    private void checkSentenceEndings(String text, List<ErrorItem> errors) {
        String[] parts = text.split("\\n");
        int index = 0;
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && trimmed.length() > 12) {
                char last = trimmed.charAt(trimmed.length() - 1);
                if (last != '.' && last != '!' && last != '?') {
                    int pos = index + part.length() - 1;
                    errors.add(new ErrorItem(
                            ErrorType.PUNCTUATION,
                            pos,
                            pos + 1,
                            String.valueOf(last),
                            "Речення або абзац не завершено розділовим знаком",
                            trimmed + ".",
                            false
                    ));
                }
            }
            index += part.length() + 1;
        }
    }

    private void checkMultiplePunctuation(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile("[!?]{2,}|\\.{4,}").matcher(text);
        while (m.find()) {
            errors.add(new ErrorItem(
                    ErrorType.STYLE,
                    m.start(),
                    m.end(),
                    m.group(),
                    "Надмірна кількість розділових знаків",
                    m.group().substring(0, 1),
                    true
            ));
        }
    }

    private void checkLatinFragments(String text, List<ErrorItem> errors) {
        Matcher m = Pattern.compile("\\b[a-zA-Z]{2,}\\b").matcher(text);
        while (m.find()) {
            errors.add(new ErrorItem(
                    ErrorType.STYLE,
                    m.start(),
                    m.end(),
                    m.group(),
                    "Латиниця всередині українського тексту. Перевірте доречність",
                    "",
                    false
            ));
        }
    }

    private void checkCommonRules(String text, List<ErrorItem> errors) {
        for (Map.Entry<Pattern, RuleInfo> entry : FIXED_RULES.entrySet()) {
            Matcher m = entry.getKey().matcher(text);
            RuleInfo info = entry.getValue();

            while (m.find()) {
                errors.add(new ErrorItem(
                        info.type,
                        m.start(),
                        m.end(),
                        m.group(),
                        info.message,
                        info.suggestion == null ? "" : info.suggestion,
                        info.safeAutoFix
                ));
            }
        }
    }

    private static class RuleInfo {
        ErrorType type;
        String message;
        String suggestion;
        boolean safeAutoFix;

        RuleInfo(ErrorType type, String message, String suggestion, boolean safeAutoFix) {
            this.type = type;
            this.message = message;
            this.suggestion = suggestion;
            this.safeAutoFix = safeAutoFix;
        }
    }
}
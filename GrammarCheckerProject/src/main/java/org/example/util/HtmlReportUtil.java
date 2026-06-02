package org.example.util;

import org.example.model.ErrorItem;
import org.example.model.TextStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlReportUtil {

    public static File exportReport(String text, List<ErrorItem> errors, TextStatistics statistics) throws IOException {
        File file = new File("grammar_report.html");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("""
                    <!DOCTYPE html>
                    <html lang="uk">
                    <head>
                        <meta charset="UTF-8">
                        <title>Grammar Report</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 30px; line-height: 1.5; }
                            h1, h2 { color: #1f4e79; }
                            table { border-collapse: collapse; width: 100%; margin-top: 16px; }
                            th, td { border: 1px solid #ccc; padding: 8px; text-align: left; vertical-align: top; }
                            th { background: #f2f2f2; }
                            .box { border: 1px solid #ddd; padding: 12px; border-radius: 8px; margin-bottom: 20px; background: #fafafa; }
                            pre { white-space: pre-wrap; word-wrap: break-word; background: #f9f9f9; padding: 12px; border-radius: 8px; }
                        </style>
                    </head>
                    <body>
                    <h1>Звіт перевірки граматики</h1>
                    """);

            writer.write("<div class='box'>");
            writer.write("<h2>Статистика тексту</h2>");
            writer.write("<p>Кількість символів: " + statistics.getCharacters() + "</p>");
            writer.write("<p>Кількість символів без пробілів: " + statistics.getCharactersNoSpaces() + "</p>");
            writer.write("<p>Кількість слів: " + statistics.getWords() + "</p>");
            writer.write("<p>Кількість речень: " + statistics.getSentences() + "</p>");
            writer.write("<p>Кількість абзаців: " + statistics.getParagraphs() + "</p>");
            writer.write("<p>Кількість знайдених проблем: " + errors.size() + "</p>");
            writer.write("</div>");

            writer.write("<div class='box'>");
            writer.write("<h2>Вихідний текст</h2>");
            writer.write("<pre>" + escape(text) + "</pre>");
            writer.write("</div>");

            writer.write("<h2>Список знайдених проблем</h2>");
            writer.write("<table>");
            writer.write("<tr><th>#</th><th>Тип</th><th>Фрагмент</th><th>Опис</th><th>Рекомендація</th></tr>");

            for (int i = 0; i < errors.size(); i++) {
                ErrorItem e = errors.get(i);
                writer.write("<tr>");
                writer.write("<td>" + (i + 1) + "</td>");
                writer.write("<td>" + escape(e.getType().name()) + "</td>");
                writer.write("<td>" + escape(e.getFragment()) + "</td>");
                writer.write("<td>" + escape(e.getMessage()) + "</td>");
                writer.write("<td>" + escape(e.getSuggestion()) + "</td>");
                writer.write("</tr>");
            }

            writer.write("</table>");
            writer.write("</body></html>");
        }

        return file;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
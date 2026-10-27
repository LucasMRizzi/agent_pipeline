package org.example.langchain4j.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PdfDocumentWriter {

    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float MARGIN = 54;
    private static final float FONT_SIZE = 11;
    private static final float LEADING = 15;

    private final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    public Path write(String content, Path output) throws IOException {
        Objects.requireNonNull(content, "O conteúdo do PDF não pode ser nulo.");
        Objects.requireNonNull(output, "O caminho de saída não pode ser nulo.");

        Path parent = output.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (PDDocument document = new PDDocument()) {
            List<String> lines = wrap(content);
            PDPageContentStream stream = null;
            float y = 0;

            try {
                for (String line : lines) {
                    if (stream == null || y - LEADING < MARGIN) {
                        if (stream != null) {
                            stream.endText();
                            stream.close();
                        }
                        PDPage page = new PDPage(PAGE_SIZE);
                        document.addPage(page);
                        stream = new PDPageContentStream(document, page);
                        stream.beginText();
                        stream.setFont(font, FONT_SIZE);
                        y = PAGE_SIZE.getHeight() - MARGIN;
                        stream.newLineAtOffset(MARGIN, y);
                    }

                    if (!line.isEmpty()) {
                        stream.showText(normalizeForStandardFont(line));
                    }
                    stream.newLineAtOffset(0, -LEADING);
                    y -= LEADING;
                }
            } finally {
                if (stream != null) {
                    stream.endText();
                    stream.close();
                }
            }

            document.save(output.toFile());
        }

        return output;
    }

    private List<String> wrap(String content) throws IOException {
        float maxWidth = PAGE_SIZE.getWidth() - (2 * MARGIN);
        List<String> lines = new ArrayList<>();

        for (String paragraph : content.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            if (paragraph.isBlank()) {
                lines.add("");
                continue;
            }

            StringBuilder line = new StringBuilder();
            for (String word : paragraph.trim().split("\\s+")) {
                String candidate = line.isEmpty() ? word : line + " " + word;

                if (!line.isEmpty() && font.getStringWidth(normalizeForStandardFont(candidate)) / 1000 * FONT_SIZE > maxWidth) {
                    lines.add(line.toString());
                    line.setLength(0);
                    line.append(word);
                } else {
                    line.setLength(0);
                    line.append(candidate);
                }
            }

            if (!line.isEmpty()) {
                lines.add(line.toString());
            }
        }

        return lines.isEmpty() ? List.of("") : lines;
    }

    private String normalizeForStandardFont(String text) throws IOException {
        String normalized = text
                // Aspas
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201A', '\'')
                .replace('\u201B', '\'')
                .replace('\u201C', '"')
                .replace('\u201D', '"')
                .replace('\u201E', '"')
                .replace('\u201F', '"')

                // Hífens e traços
                .replace('\u2010', '-')
                .replace('\u2011', '-')
                .replace('\u2012', '-')
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2015', '-')
                .replace('\u2212', '-')

                // Espaços
                .replace('\u00A0', ' ')
                .replace('\u1680', ' ')
                .replaceAll("[\\u2000-\\u200A]", " ")
                .replace('\u202F', ' ')
                .replace('\u205F', ' ')
                .replace('\u3000', ' ')
                .replace("\t", "    ")

                // Quebras de linha
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u0085', '\n')
                .replace('\u2028', '\n')
                .replace('\u2029', '\n')

                // Caracteres invisíveis e controles de direção
                .replaceAll(
                        "[\\u00AD\\u200B-\\u200F\\u202A-\\u202E"
                                + "\\u2060-\\u206F\\uFEFF]",
                        ""
                )

                // Marcadores
                .replace('\u2022', '-')
                .replace('\u2023', '-')
                .replace('\u2043', '-')
                .replace('\u25AA', '-')
                .replace('\u25CF', '-')
                .replace('\u25CB', '-')

                // Matemática
                .replace("\u2265", ">=")
                .replace("\u2264", "<=")
                .replace("\u2260", "!=")
                .replace("\u2248", "~=")
                .replace("\u2261", "===")
                .replace("\u221E", "infinito")
                .replace("\u221A", "sqrt")
                .replace("\u2211", "soma")
                .replace("\u220F", "produto")
                .replace("\u2208", " pertence a ")
                .replace("\u2209", " nao pertence a ")
                .replace("\u2205", "conjunto vazio")

                // Setas
                .replace("\u2190", "<-")
                .replace("\u2192", "->")
                .replace("\u2194", "<->")
                .replace("\u21D0", "<=")
                .replace("\u21D2", "=>")
                .replace("\u21D4", "<=>")
                .replace("\u2191", "[cima]")
                .replace("\u2193", "[baixo]")

                // Outros símbolos
                .replace("\u2026", "...")
                .replace("\u2713", "[OK]")
                .replace("\u2714", "[OK]")
                .replace("\u2717", "[X]")
                .replace("\u2718", "[X]")
                .replace("\u2610", "[ ]")
                .replace("\u2611", "[X]")
                .replace("\u2612", "[X]");

        StringBuilder result = new StringBuilder();

        for (int offset = 0; offset < normalized.length();) {
            int codePoint = normalized.codePointAt(offset);
            offset += Character.charCount(codePoint);

            // Preserva quebras para o wrap separar os parágrafos.
            if (codePoint == '\n') {
                result.append('\n');
                continue;
            }

            if (Character.isISOControl(codePoint)) {
                continue;
            }

            String character = new String(Character.toChars(codePoint));

            try {
                // Verifica se a fonte efetivamente aceita o caractere.
                font.getStringWidth(character);
                result.append(character);
            } catch (IllegalArgumentException e) {
                result.append('?');
            }
        }

        return result.toString();
    }
}

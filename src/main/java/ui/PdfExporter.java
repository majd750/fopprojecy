package ui;

import model.DiaryEntry;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfExporter {

    public static void exportEntriesToPDF(List<DiaryEntry> entries, File outputFile) {
        try (PDDocument document = new PDDocument()) {

            PDType1Font font = PDType1Font.HELVETICA;
            float fontSize = 12;
            float margin = 50;
            float leading = 1.5f * fontSize;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(font, fontSize);
            contentStream.beginText();

            float yStart = page.getMediaBox().getHeight() - margin;
            float xStart = margin;
            contentStream.newLineAtOffset(xStart, yStart);
            float yPosition = yStart;

            for (DiaryEntry entry : entries) {
                String[] fixedLines = {
                        "Title: " + entry.getTitle(),
                        "Date: " + entry.getCreatedDate().toString(),
                        "Mood: " + entry.getMood()
                };

                // Print fixed fields
                for (String line : fixedLines) {
                    if (yPosition <= margin) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(font, fontSize);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(xStart, yStart);
                        yPosition = yStart;
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                }

                // Print content if not empty
                String content = entry.getContent();
                if (content != null && !content.trim().isEmpty()) {
                    if (yPosition <= margin) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(font, fontSize);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(xStart, yStart);
                        yPosition = yStart;
                    }
                    contentStream.showText("Content:");
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;

                    List<String> wrappedLines = wrapText(content, 90);
                    for (String line : wrappedLines) {
                        if (yPosition <= margin) {
                            contentStream.endText();
                            contentStream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            contentStream.setFont(font, fontSize);
                            contentStream.beginText();
                            contentStream.newLineAtOffset(xStart, yStart);
                            yPosition = yStart;
                        }
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -leading);
                        yPosition -= leading;
                    }
                }

                // Add spacing between entries
                contentStream.newLineAtOffset(0, -leading);
                yPosition -= leading;
            }

            contentStream.endText();
            contentStream.close();
            document.save(outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> wrapText(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\n")) {
            while (paragraph.length() > maxLineLength) {
                int wrapAt = paragraph.lastIndexOf(' ', maxLineLength);
                if (wrapAt == -1) wrapAt = maxLineLength;
                lines.add(paragraph.substring(0, wrapAt));
                paragraph = paragraph.substring(wrapAt).trim();
            }
            lines.add(paragraph);
        }
        return lines;
    }
}

package com.clausetrail.service;

import com.clausetrail.dto.ClauseDTO;
import com.clausetrail.model.Clause;
import com.clausetrail.model.Contract;
import com.clausetrail.model.ContractVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParserService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserService.class);

    public String extractTextFromFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        try (InputStream inputStream = file.getInputStream()) {
            if (filename.endsWith(".pdf")) {
                byte[] bytes = file.getBytes();
                try (PDDocument doc = Loader.loadPDF(bytes)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(doc);
                }
            } else if (filename.endsWith(".docx")) {
                try (XWPFDocument docx = new XWPFDocument(inputStream)) {
                    StringBuilder sb = new StringBuilder();
                    for (XWPFParagraph p : docx.getParagraphs()) {
                        sb.append(p.getText()).append("\n\n");
                    }
                    return sb.toString();
                }
            } else {
                return new String(file.getBytes());
            }
        }
    }

    public List<ClauseDTO> segmentIntoClauses(String fullText) {
        List<ClauseDTO> clauses = new ArrayList<>();
        if (fullText == null || fullText.isBlank()) {
            return clauses;
        }

        // Pattern to identify legal sections / clauses (e.g. "1. Term", "Section 2.", "Article III", "Clause 4 - ")
        Pattern pattern = Pattern.compile("(?m)^(?=(?:(?:Section|Article|Clause)\\s+)?(\\d+|[IVXLCDM]+|[A-Z])\\.?\\s+([A-Z][^\\n\\r:]+)(?:[:\\.\\-]|$))");
        String[] parts = fullText.split("(?m)^(?=(?:(?:Section|Article|Clause)\\s+)?(\\d+|[IVXLCDM]+|[A-Z])\\.?\\s+([A-Z][^\\n\\r:]+)(?:[:\\.\\-]|$))");

        if (parts.length <= 1) {
            // Fallback: split by numbered lines or double newlines
            String[] paragraphs = fullText.split("\n\\s*\n");
            int idx = 1;
            for (String p : paragraphs) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) {
                    String title = "Section " + idx;
                    if (trimmed.length() < 60) {
                        title = trimmed;
                    } else {
                        String firstLine = trimmed.split("\n")[0];
                        if (firstLine.length() < 60) {
                            title = firstLine;
                        }
                    }
                    clauses.add(ClauseDTO.builder()
                            .id("clause-" + UUID.randomUUID().toString().substring(0, 8))
                            .orderIndex(idx)
                            .clauseNumber(String.valueOf(idx))
                            .title(title)
                            .content(trimmed)
                            .category("General")
                            .build());
                    idx++;
                }
            }
        } else {
            int idx = 1;
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    String[] lines = trimmed.split("\n", 2);
                    String header = lines[0].trim();
                    String body = lines.length > 1 ? lines[1].trim() : trimmed;

                    clauses.add(ClauseDTO.builder()
                            .id("clause-" + UUID.randomUUID().toString().substring(0, 8))
                            .orderIndex(idx)
                            .clauseNumber(String.valueOf(idx))
                            .title(header)
                            .content(trimmed)
                            .category("General")
                            .build());
                    idx++;
                }
            }
        }

        return clauses;
    }

    public byte[] generateContractPdf(Contract contract, ContractVersion version) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            float y = 780;
            float margin = 50;
            float width = page.getMediaBox().getWidth() - 2 * margin;

            // Header Banner
            contentStream.beginText();
            contentStream.setFont(fontBold, 18);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText(sanitizeText(contract.getTitle() != null ? contract.getTitle() : "Legal Contract Document"));
            contentStream.endText();

            y -= 25;
            contentStream.beginText();
            contentStream.setFont(fontRegular, 11);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Contract Type: " + sanitizeText(contract.getContractType() != null ? contract.getContractType() : "Standard Agreement"));
            contentStream.endText();

            y -= 16;
            contentStream.beginText();
            contentStream.setFont(fontRegular, 11);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Version: v" + version.getVersionNumber() + " | Status: " + version.getStatus());
            contentStream.endText();

            y -= 16;
            contentStream.beginText();
            contentStream.setFont(fontRegular, 11);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Parties: " + sanitizeText(String.join(", ", contract.getParties())));
            contentStream.endText();

            y -= 20;
            // Horizontal rule
            contentStream.setLineWidth(1.0f);
            contentStream.moveTo(margin, y);
            contentStream.lineTo(margin + width, y);
            contentStream.stroke();

            y -= 25;

            List<Clause> clauses = version.getClauses();
            if (clauses != null && !clauses.isEmpty()) {
                for (Clause c : clauses) {
                    if (y < 80) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        y = 780;
                    }

                    // Clause Header
                    contentStream.beginText();
                    contentStream.setFont(fontBold, 12);
                    contentStream.newLineAtOffset(margin, y);
                    String clauseHeader = (c.getClauseNumber() != null ? c.getClauseNumber() + ". " : "") +
                            (c.getTitle() != null ? c.getTitle() : "Clause");
                    contentStream.showText(sanitizeText(clauseHeader));
                    contentStream.endText();
                    y -= 16;

                    // Clause Body (wrapped text)
                    List<String> wrappedLines = wrapText(sanitizeText(c.getContent()), 85);
                    for (String line : wrappedLines) {
                        if (y < 60) {
                            contentStream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);
                            y = 780;
                        }
                        contentStream.beginText();
                        contentStream.setFont(fontRegular, 10);
                        contentStream.newLineAtOffset(margin + 10, y);
                        contentStream.showText(line);
                        contentStream.endText();
                        y -= 14;
                    }
                    y -= 12;
                }
            } else if (version.getFullText() != null) {
                List<String> wrappedLines = wrapText(sanitizeText(version.getFullText()), 85);
                for (String line : wrappedLines) {
                    if (y < 60) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        y = 780;
                    }
                    contentStream.beginText();
                    contentStream.setFont(fontRegular, 10);
                    contentStream.newLineAtOffset(margin, y);
                    contentStream.showText(line);
                    contentStream.endText();
                    y -= 14;
                }
            }

            contentStream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private String sanitizeText(String text) {
        if (text == null) return "";
        return text.replace("\r", "")
                .replaceAll("[^\\x20-\\x7E\\n]", " ");
    }

    private List<String> wrapText(String text, int maxCharsPerLine) {
        List<String> result = new ArrayList<>();
        if (text == null) return result;

        String[] paragraphs = text.split("\n");
        for (String para : paragraphs) {
            String[] words = para.split("\\s+");
            StringBuilder currentLine = new StringBuilder();
            for (String word : words) {
                if (currentLine.length() + word.length() + 1 > maxCharsPerLine) {
                    if (currentLine.length() > 0) {
                        result.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }
                }
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
            if (currentLine.length() > 0) {
                result.add(currentLine.toString());
            }
        }
        return result;
    }
}

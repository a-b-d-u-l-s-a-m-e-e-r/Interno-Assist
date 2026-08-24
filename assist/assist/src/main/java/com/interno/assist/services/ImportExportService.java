package com.interno.assist.services;

import com.interno.assist.dto.ExportRequestDto;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Chunk;
import org.openpdf.text.List;
import org.openpdf.text.ListItem;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("importExportService")
public class ImportExportService {

    // =====================================================
    // MAIN PDF GENERATION
    // =====================================================

    public byte[] processDownload(
            ExportRequestDto requestDto
    ) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            // =====================================================
            // DOCUMENT
            // =====================================================

            Document document =
                    new Document();

            document.setMargins(
                    50,
                    50,
                    60,
                    50
            );

            // =====================================================
            // PDF WRITER
            // =====================================================

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            outputStream
                    );

            // =====================================================
            // PAGE NUMBER
            // =====================================================

            writer.setPageEvent(
                    new PageNumberEvent()
            );

            document.open();

            // =====================================================
            // FONTS
            // =====================================================

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            22,
                            Font.BOLD
                    );

            Font subtitleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10,
                            Font.NORMAL
                    );

            Font noteHeadingFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            16,
                            Font.BOLD
                    );

            Font dateFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9,
                            Font.ITALIC
                    );

            Font heading1Font =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            16,
                            Font.BOLD
                    );

            Font heading2Font =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            13,
                            Font.BOLD
                    );

            Font heading3Font =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            Font.BOLD
                    );

            Font contentFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11,
                            Font.NORMAL
                    );

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            Font.BOLD
                    );

            Font italicFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_OBLIQUE,
                            11,
                            Font.ITALIC
                    );

            Font codeFont =
                    FontFactory.getFont(
                            FontFactory.COURIER,
                            9,
                            Font.NORMAL
                    );

            // =====================================================
            // DOCUMENT TITLE
            // =====================================================

            Paragraph title =
                    new Paragraph(
                            "Interno-Assist Research Notes",
                            titleFont
                    );

            title.setAlignment(
                    Element.ALIGN_CENTER
            );

            title.setSpacingAfter(
                    8
            );

            document.add(title);

            // =====================================================
            // GENERATED DATE
            // =====================================================

            String generatedDate =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM yyyy, hh:mm a"
                                    )
                            );

            Paragraph generated =
                    new Paragraph(
                            "Generated on " +
                                    generatedDate,
                            subtitleFont
                    );

            generated.setAlignment(
                    Element.ALIGN_CENTER
            );

            generated.setSpacingAfter(
                    25
            );

            document.add(generated);

            // =====================================================
            // GET CONTENT
            // =====================================================

            String content =
                    requestDto.getContent();

            if (
                    content == null ||
                            content.trim().isEmpty()
            ) {

                document.add(
                        new Paragraph(
                                "No notes available.",
                                contentFont
                        )
                );

            } else {

                // =================================================
                // SPLIT NOTES
                // =================================================

                String[] notes =
                        content.split(
                                "\\n\\n----------------------------------------\\n\\n"
                        );

                for (
                        int i = 0;
                        i < notes.length;
                        i++
                ) {

                    addNote(
                            document,
                            notes[i],
                            noteHeadingFont,
                            dateFont,
                            heading1Font,
                            heading2Font,
                            heading3Font,
                            contentFont,
                            boldFont,
                            italicFont,
                            codeFont
                    );

                    // =============================================
                    // NOTE SEPARATOR
                    // =============================================

                    if (
                            i < notes.length - 1
                    ) {

                        Paragraph separator =
                                new Paragraph(
                                        "────────────────────────────────────────",
                                        dateFont
                                );

                        separator.setAlignment(
                                Element.ALIGN_CENTER
                        );

                        separator.setSpacingBefore(
                                15
                        );

                        separator.setSpacingAfter(
                                15
                        );

                        document.add(
                                separator
                        );
                    }
                }
            }

            // =====================================================
            // CLOSE
            // =====================================================

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate PDF",
                    e
            );
        }
    }

    // =====================================================
    // ADD ONE NOTE
    // =====================================================

    private void addNote(
            Document document,
            String note,
            Font noteHeadingFont,
            Font dateFont,
            Font heading1Font,
            Font heading2Font,
            Font heading3Font,
            Font contentFont,
            Font boldFont,
            Font italicFont,
            Font codeFont
    ) {

        if (
                note == null ||
                        note.trim().isEmpty()
        ) {
            return;
        }

        String[] lines =
                note.split(
                        "\\r?\\n",
                        -1
                );

        // =====================================================
        // NOTE TITLE
        // =====================================================

        if (lines.length > 0) {

            String noteTitle =
                    lines[0].trim();

            Paragraph heading =
                    new Paragraph(
                            noteTitle,
                            noteHeadingFont
                    );

            heading.setSpacingBefore(
                    5
            );

            heading.setSpacingAfter(
                    6
            );

            document.add(
                    heading
            );
        }

        // =====================================================
        // NOTE DATE
        // =====================================================

        int contentStart = 1;

        if (
                lines.length > 1 &&
                        lines[1]
                                .trim()
                                .startsWith("Date:")
        ) {

            Paragraph dateParagraph =
                    new Paragraph(
                            lines[1].trim(),
                            dateFont
                    );

            dateParagraph.setSpacingAfter(
                    12
            );

            document.add(
                    dateParagraph
            );

            contentStart = 2;
        }

        // =====================================================
        // MARKDOWN CONTENT
        // =====================================================

        StringBuilder markdown =
                new StringBuilder();

        for (
                int i = contentStart;
                i < lines.length;
                i++
        ) {

            markdown.append(
                    lines[i]
            );

            if (
                    i < lines.length - 1
            ) {
                markdown.append(
                        "\n"
                );
            }
        }

        parseMarkdown(
                document,
                markdown.toString(),
                heading1Font,
                heading2Font,
                heading3Font,
                contentFont,
                boldFont,
                italicFont,
                codeFont
        );
    }

    // =====================================================
    // MARKDOWN PARSER
    // =====================================================

    private void parseMarkdown(
            Document document,
            String markdown,
            Font heading1Font,
            Font heading2Font,
            Font heading3Font,
            Font contentFont,
            Font boldFont,
            Font italicFont,
            Font codeFont
    ) {

        if (
                markdown == null ||
                        markdown.trim().isEmpty()
        ) {
            return;
        }

        String[] lines =
                markdown.split(
                        "\\r?\\n",
                        -1
                );

        List bulletList = null;
        List numberedList = null;

        for (
                String line : lines
        ) {

            String trimmed =
                    line.trim();

            // =================================================
            // EMPTY LINE
            // =================================================

            if (trimmed.isEmpty()) {

                if (bulletList != null) {

                    document.add(
                            bulletList
                    );

                    bulletList = null;
                }

                if (numberedList != null) {

                    document.add(
                            numberedList
                    );

                    numberedList = null;
                }

                continue;
            }

            // =================================================
            // HEADING 3
            // =================================================

            if (
                    trimmed.startsWith("### ")
            ) {

                flushLists(
                        document,
                        bulletList,
                        numberedList
                );

                bulletList = null;
                numberedList = null;

                String text =
                        trimmed.substring(4);

                Paragraph paragraph =
                        new Paragraph(
                                parseInlineMarkdown(
                                        text,
                                        contentFont,
                                        boldFont,
                                        italicFont,
                                        codeFont
                                )
                        );

                paragraph.setFont(
                        heading3Font
                );

                paragraph.setSpacingBefore(
                        10
                );

                paragraph.setSpacingAfter(
                        6
                );

                document.add(
                        paragraph
                );

                continue;
            }

            // =================================================
            // HEADING 2
            // =================================================

            if (
                    trimmed.startsWith("## ")
            ) {

                flushLists(
                        document,
                        bulletList,
                        numberedList
                );

                bulletList = null;
                numberedList = null;

                String text =
                        trimmed.substring(3);

                Paragraph paragraph =
                        new Paragraph(
                                parseInlineMarkdown(
                                        text,
                                        contentFont,
                                        boldFont,
                                        italicFont,
                                        codeFont
                                )
                        );

                paragraph.setFont(
                        heading2Font
                );

                paragraph.setSpacingBefore(
                        12
                );

                paragraph.setSpacingAfter(
                        7
                );

                document.add(
                        paragraph
                );

                continue;
            }

            // =================================================
            // HEADING 1
            // =================================================

            if (
                    trimmed.startsWith("# ")
            ) {

                flushLists(
                        document,
                        bulletList,
                        numberedList
                );

                bulletList = null;
                numberedList = null;

                String text =
                        trimmed.substring(2);

                Paragraph paragraph =
                        new Paragraph(
                                parseInlineMarkdown(
                                        text,
                                        contentFont,
                                        boldFont,
                                        italicFont,
                                        codeFont
                                )
                        );

                paragraph.setFont(
                        heading1Font
                );

                paragraph.setSpacingBefore(
                        14
                );

                paragraph.setSpacingAfter(
                        8
                );

                document.add(
                        paragraph
                );

                continue;
            }

            // =================================================
            // BULLET LIST
            // =================================================

            if (
                    trimmed.startsWith("- ") ||
                            trimmed.startsWith("* ") ||
                            trimmed.startsWith("• ")
            ) {

                if (numberedList != null) {

                    document.add(
                            numberedList
                    );

                    numberedList = null;
                }

                if (bulletList == null) {

                    bulletList =
                            new List(
                                    List.UNORDERED
                            );

                    bulletList.setIndentationLeft(
                            20
                    );

                    bulletList.setSymbolIndent(
                            10
                    );
                }

                String text =
                        trimmed.substring(2);

                ListItem item =
                        new ListItem();

                item.add(
                        parseInlineMarkdown(
                                text,
                                contentFont,
                                boldFont,
                                italicFont,
                                codeFont
                        )
                );

                bulletList.add(
                        item
                );

                continue;
            }

            // =================================================
            // NUMBERED LIST
            // =================================================

            if (
                    trimmed.matches(
                            "^\\d+\\.\\s+.*"
                    )
            ) {

                if (bulletList != null) {

                    document.add(
                            bulletList
                    );

                    bulletList = null;
                }

                if (numberedList == null) {

                    numberedList =
                            new List(
                                    List.ORDERED
                            );

                    numberedList.setIndentationLeft(
                            20
                    );

                    numberedList.setSymbolIndent(
                            10
                    );
                }

                String text =
                        trimmed.replaceFirst(
                                "^\\d+\\.\\s+",
                                ""
                        );

                ListItem item =
                        new ListItem();

                item.add(
                        parseInlineMarkdown(
                                text,
                                contentFont,
                                boldFont,
                                italicFont,
                                codeFont
                        )
                );

                numberedList.add(
                        item
                );

                continue;
            }

            // =================================================
            // NORMAL PARAGRAPH
            // =================================================

            flushLists(
                    document,
                    bulletList,
                    numberedList
            );

            bulletList = null;
            numberedList = null;

            Paragraph paragraph =
                    new Paragraph();

            paragraph.setFont(
                    contentFont
            );

            paragraph.setLeading(
                    15
            );

            paragraph.setSpacingAfter(
                    8
            );

            paragraph.add(
                    parseInlineMarkdown(
                            trimmed,
                            contentFont,
                            boldFont,
                            italicFont,
                            codeFont
                    )
            );

            document.add(
                    paragraph
            );
        }

        // =====================================================
        // FLUSH REMAINING LISTS
        // =====================================================

        flushLists(
                document,
                bulletList,
                numberedList
        );
    }

    // =====================================================
    // FLUSH LISTS
    // =====================================================

    private void flushLists(
            Document document,
            List bulletList,
            List numberedList
    ) {

        if (bulletList != null) {

            document.add(
                    bulletList
            );
        }

        if (numberedList != null) {

            document.add(
                    numberedList
            );
        }
    }

    // =====================================================
    // INLINE MARKDOWN
    // =====================================================

    private Phrase parseInlineMarkdown(
            String text,
            Font normalFont,
            Font boldFont,
            Font italicFont,
            Font codeFont
    ) {

        Phrase phrase =
                new Phrase();

        if (
                text == null ||
                        text.isEmpty()
        ) {
            return phrase;
        }

        int position = 0;

        while (
                position < text.length()
        ) {

            // =================================================
            // BOLD
            // =================================================

            if (
                    text.startsWith(
                            "**",
                            position
                    )
            ) {

                int end =
                        text.indexOf(
                                "**",
                                position + 2
                        );

                if (end != -1) {

                    String boldText =
                            text.substring(
                                    position + 2,
                                    end
                            );

                    phrase.add(
                            new Chunk(
                                    boldText,
                                    boldFont
                            )
                    );

                    position =
                            end + 2;

                    continue;
                }
            }

            // =================================================
            // INLINE CODE
            // =================================================

            if (
                    text.charAt(position) == '`'
            ) {

                int end =
                        text.indexOf(
                                '`',
                                position + 1
                        );

                if (end != -1) {

                    String codeText =
                            text.substring(
                                    position + 1,
                                    end
                            );

                    phrase.add(
                            new Chunk(
                                    codeText,
                                    codeFont
                            )
                    );

                    position =
                            end + 1;

                    continue;
                }
            }

            // =================================================
            // ITALIC
            // =================================================

            if (
                    text.charAt(position) == '*'
            ) {

                int end =
                        text.indexOf(
                                '*',
                                position + 1
                        );

                if (end != -1) {

                    String italicText =
                            text.substring(
                                    position + 1,
                                    end
                            );

                    phrase.add(
                            new Chunk(
                                    italicText,
                                    italicFont
                            )
                    );

                    position =
                            end + 1;

                    continue;
                }
            }

            // =================================================
            // NORMAL TEXT
            // =================================================

            int nextSpecial =
                    findNextSpecialCharacter(
                            text,
                            position
                    );

            if (
                    nextSpecial == -1
            ) {

                phrase.add(
                        new Chunk(
                                text.substring(position),
                                normalFont
                        )
                );

                break;
            }

            if (
                    nextSpecial > position
            ) {

                phrase.add(
                        new Chunk(
                                text.substring(
                                        position,
                                        nextSpecial
                                ),
                                normalFont
                        )
                );
            }

            position =
                    nextSpecial;
        }

        return phrase;
    }

    // =====================================================
    // FIND NEXT MARKDOWN CHARACTER
    // =====================================================

    private int findNextSpecialCharacter(
            String text,
            int start
    ) {

        for (
                int i = start;
                i < text.length();
                i++
        ) {

            char c =
                    text.charAt(i);

            if (
                    c == '*' ||
                            c == '`'
            ) {

                return i;
            }
        }

        return -1;
    }

    // =====================================================
    // PAGE NUMBER EVENT
    // =====================================================

    private static class PageNumberEvent
            extends PdfPageEventHelper {

        private final Font footerFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        8,
                        Font.NORMAL
                );

        @Override
        public void onEndPage(
                PdfWriter writer,
                Document document
        ) {

            float x =
                    (
                            document.left()
                                    + document.right()
                    ) / 2;

            float y =
                    document.bottom() - 30;

            writer.getDirectContent()
                    .beginText();

            writer.getDirectContent()
                    .setFontAndSize(
                            footerFont.getBaseFont(),
                            8
                    );

            writer.getDirectContent()
                    .showTextAligned(
                            Element.ALIGN_CENTER,
                            "Interno-Assist  •  Page "
                                    + writer.getPageNumber(),
                            x,
                            y,
                            0
                    );

            writer.getDirectContent()
                    .endText();
        }
    }
}
package com.aua.museum.booking.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class FooterEventHandler extends PdfPageEventHelper {
    private final Font pageNumberFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Phrase pagePhrase = new Phrase("-" + document.getPageNumber() + "-", pageNumberFont);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, pagePhrase, 550, 30, 0);
    }
}

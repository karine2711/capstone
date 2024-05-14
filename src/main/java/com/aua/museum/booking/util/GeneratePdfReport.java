package com.aua.museum.booking.util;

import com.aua.museum.booking.domain.Event;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GeneratePdfReport {

    public static final String EVENT_DISPLAY_VALUE = "Event";

    private GeneratePdfReport(){}

    public static final String UNICODE_PATH = "src/main/resources/static/font/arial.ttf";

    private static BaseFont unicode;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    static {
        try {
            unicode = BaseFont.createFont(UNICODE_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateReport(List<Event> events, HttpServletRequest request,
                                      HttpServletResponse response, Locale locale, MessageSource messageSource) throws IOException {
        String startDateString = request.getParameter("date1").replace("-", "/");
        String endDateString = request.getParameter("date2").replace("-", "/");
        String eventDateRange = startDateString + " - " + endDateString;
        Document document = new Document();


        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            Font unicodeFont = new Font(unicode, 14, Font.BOLD);
            Font unicodeFont1 = new Font(unicode, 11, Font.BOLD);
            Font unicodeFont2 = new Font(unicode, 10);
            Font font1Ext = FontFactory.getFont(FontFactory.TIMES, 12, Font.BOLD, BaseColor.BLACK);
            Font font3 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
            com.aua.museum.booking.util.FooterEventHandler eventHandler = new com.aua.museum.booking.util.FooterEventHandler();

            String orgName = messageSource.getMessage("generalInfo.name", new Object[]{}, locale);

            Chunk organizationName = new Chunk(orgName, unicodeFont);
            Chunk eventDatesRange = new Chunk(eventDateRange, font1Ext);
            eventDatesRange.setUnderline(1f, -1.5f);

            Paragraph emptyParagraph = new Paragraph();
            Paragraph paragraph = new Paragraph();
            paragraph.setIndentationLeft(40f);
            paragraph.setIndentationRight(40f);
            paragraph.add(organizationName);
            paragraph.setLeading(15f);

            String imageFile = "src/main/resources/static/images/black-white-logo.png";
            Image img = Image.getInstance(imageFile);
            img.setAlignment(Element.ALIGN_RIGHT);
            img.scaleToFit(30f, 30f);
            document.add(img);

            Paragraph dateParagraph = new Paragraph();
            dateParagraph.setIndentationLeft(40f);
            dateParagraph.add(eventDatesRange);

            Paragraph listOfEventsParagraph = new Paragraph();
            List<String> eventFiltersInfo;
            if (request.getParameter("eventFilters") != null) eventFiltersInfo = Arrays
                    .asList(request.getParameter("eventFilters").split(","));
            else eventFiltersInfo = new ArrayList<>();

            Chunk chunkOfEvents;
            StringBuilder chunkOfEventsContent = null;
            if (!eventFiltersInfo.isEmpty()) {
                chunkOfEventsContent = createEventsList(eventFiltersInfo, messageSource, locale);
                chunkOfEventsContent.deleteCharAt(chunkOfEventsContent.length() - 1);
            }

            String defaultEventList = messageSource.getMessage("downloadPdf.defaultEventsList", new Object[]{}, locale);
            String eventList = messageSource.getMessage("downloadPdf.eventsList", new Object[]{}, locale);
            listOfEventsParagraph.setIndentationLeft(40f);
            if (chunkOfEventsContent == null || eventFiltersInfo.size() == 7) chunkOfEvents =
                    new Chunk(defaultEventList, unicodeFont1);
            else chunkOfEvents =
                    new Chunk(eventList.replace("-", chunkOfEventsContent.toString()), unicodeFont1);
            listOfEventsParagraph.setLeading(25f);

            listOfEventsParagraph.add(chunkOfEvents);

            document.add(emptyParagraph);
            document.add(Chunk.NEWLINE);
            document.add(paragraph);
            document.add(dateParagraph);
            document.add(listOfEventsParagraph);

            LocalDate eventDate = null;
            for (Event event : events) {
                String time = event.getTime().format(timeFormatter);
                String endTime = getEventEndTime(event);
                Paragraph dailyDateParagraph = new Paragraph();
                if (eventDate == null || event.getDate().isAfter(eventDate)) {
                    Chunk dailyDateChunk = new Chunk(event.getDate().format(dateFormatter), font1Ext);
                    eventDate = event.getDate();
                    dailyDateParagraph.add(dailyDateChunk);
                    dailyDateParagraph.setLeading(35f);
                    dailyDateParagraph.setIndentationLeft(40f);
                }
                Paragraph eventParagraph = new Paragraph();
                eventParagraph.setIndentationLeft(40f);
                Chunk chunk = new Chunk(time + " - " + endTime + " ------------------------------------------------------------" +
                        "------------------------------------------------------", font3);

                Paragraph eventParagraph1 = new Paragraph();
                String groupSize = messageSource.getMessage("downloadPdf.groupSize", new Object[]{}, locale);
                groupSize = groupSize.replace("-", String.valueOf(event.getGroupSize()));

                if (event.getGroupSize() != null) {
                    eventParagraph1.setIndentationLeft(110f);
                    Chunk chunk1 = new Chunk(groupSize, unicodeFont1);
                    eventParagraph1.add(chunk1);
                }

                Paragraph eventParagraph3 = new Paragraph();
                eventParagraph3.setIndentationLeft(110f);
                Font eventTypeFont = new Font(unicode, 12, Font.BOLD, BaseColor.BLACK);
                String eventType = getEventTypeInDifferentLanguages(event, locale, messageSource);
                Chunk chunk3 = new Chunk(eventType, eventTypeFont);

                Paragraph titleParagraph = new Paragraph();
                if (event.getEventType().getDisplayValueEN().equals(EVENT_DISPLAY_VALUE)) {
                    titleParagraph.setIndentationLeft(110f);
                    Phrase phrase = new Phrase(event.getTitleByLocale(locale), unicodeFont1);
                    titleParagraph.add(phrase);
                }

                Paragraph eventParagraph5 = new Paragraph();
                if (event.getDescriptionByLocale(locale) != null && event.getEventType().getDisplayValueEN().equals(EVENT_DISPLAY_VALUE)) {
                    eventParagraph5.setIndentationLeft(110f);
                    Phrase phrase = new Phrase(event.getDescriptionByLocale(locale), unicodeFont2);
                    eventParagraph5.add(phrase);
                }

                Paragraph schoolParagraph = new Paragraph();
                schoolParagraph.setIndentationLeft(110f);
                Phrase schoolPhrase = new Phrase(event.getSchool());
                schoolParagraph.add(schoolPhrase);
                schoolParagraph.setLeading(20f);

                Paragraph classParagraph = new Paragraph();
                classParagraph.setIndentationLeft(110f);
                Phrase classPhrase = new Phrase(event.getGroup());
                classParagraph.add(classPhrase);
                classParagraph.setLeading(20f);


                eventParagraph.setLeading(20f);
                eventParagraph.add(chunk);
                eventParagraph1.setLeading(15f);
                if (!event.getEventType().getDisplayValueEN().equals(EVENT_DISPLAY_VALUE)) {
                    eventParagraph3.add(chunk3);
                    eventParagraph3.setLeading(16f);
                }
                titleParagraph.setLeading(16f);
                eventParagraph5.setLeading(20f);


                document.add(dailyDateParagraph);
                document.add(eventParagraph);
                document.add(eventParagraph1);
                document.add(eventParagraph3);
                document.add(titleParagraph);
                document.add(eventParagraph5);
                document.add(schoolParagraph);
                document.add(classParagraph);
                pdfWriter.setPageEvent(eventHandler);
            }
            response.flushBuffer();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        } finally {
            document.close();
        }

    }

    private static StringBuilder createEventsList(List<String> events, MessageSource messageSource, Locale locale) {
        StringBuilder eventsList = new StringBuilder();
        events.forEach(event -> {
            switch (event) {
                case "preschool":
                    eventsList.append(messageSource.getMessage("downloadPdf.preschool", new Object[]{}, locale)).append(",");
                    break;
                case "elementary":
                    eventsList.append(messageSource.getMessage("downloadPdf.elementary", new Object[]{}, locale)).append(",");
                    break;
                case "middle":
                    eventsList.append(messageSource.getMessage("downloadPdf.middleSchool", new Object[]{}, locale)).append(",");
                    break;
                case "high":
                    eventsList.append(messageSource.getMessage("downloadPdf.highSchool", new Object[]{}, locale)).append(",");
                    break;
                case "students":
                    eventsList.append(messageSource.getMessage("downloadPdf.students", new Object[]{}, locale)).append(",");
                    break;
                case "individuals":
                    eventsList.append(messageSource.getMessage("downloadPdf.individuals", new Object[]{}, locale)).append(",");
                    break;
                case "event":
                    eventsList.append(messageSource.getMessage("downloadPdf.event", new Object[]{}, locale)).append(",");
                    break;
                default:
                    break;
            }
        });
        return eventsList;
    }

    protected static String getEventTypeInDifferentLanguages(Event event, Locale locale, MessageSource messageSource) {
        return switch (event.getEventType().getDisplayValueEN()) {
            case EVENT_DISPLAY_VALUE -> messageSource.getMessage("downloadPdf.event", new Object[]{}, locale);
            case "Individuals" -> messageSource.getMessage("downloadPdf.individuals", new Object[]{}, locale);
            case "Students" -> messageSource.getMessage("downloadPdf.students", new Object[]{}, locale);
            case "High" -> messageSource.getMessage("downloadPdf.highSchool", new Object[]{}, locale);
            case "Middle" -> messageSource.getMessage("downloadPdf.middleSchool", new Object[]{}, locale);
            case "Elementary" -> messageSource.getMessage("downloadPdf.elementary", new Object[]{}, locale);
            case "Preschool" -> messageSource.getMessage("downloadPdf.preschool", new Object[]{}, locale);
            default -> "";
        };
    }

    private static String getEventEndTime(Event event) {
        return event.getTime().plusMinutes(event.getEventType().getDuration()).format(timeFormatter);
    }
}

package com.aua.museum.booking.util;

import com.aua.museum.booking.domain.Event;
import com.opencsv.CSVWriter;
import org.springframework.context.MessageSource;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class WriteData {

    private WriteData() {
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static void generateReport(PrintWriter writer, List<Event> events, Locale locale, MessageSource messageSource) {

        String utf16 = "\uFEFF";
        String[] csvHeader = {
                messageSource.getMessage("downloadCsv.EventType", new Object[]{}, locale),//eventtype
                messageSource.getMessage("downloadCsv.Date", new Object[]{}, locale),//"Date"
                messageSource.getMessage("downloadCsv.startTime", new Object[]{}, locale),// , "Start Time",
                messageSource.getMessage("downloadCsv.endTime", new Object[]{}, locale),// , "End Time",
                messageSource.getMessage("downloadCsv.School", new Object[]{}, locale),// "School"
                messageSource.getMessage("downloadCsv.Class", new Object[]{}, locale),// ,"Class"
                messageSource.getMessage("downloadCsv.GroupSize", new Object[]{}, locale),// ,"GroupSize",
                messageSource.getMessage("downloadCsv.Title", new Object[]{}, locale),// "Title"
                messageSource.getMessage("downloadCsv.Description", new Object[]{}, locale),// "Event Description"
        };
        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDate = new SimpleDateFormat("dd.MM.yyyy");

        writer.write(utf16);
        try (
                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)
        ) {

            csvWriter.writeNext(csvHeader);
            for (Event event : events) {
                String[] data = {
                        com.aua.museum.booking.util.GeneratePdfReport.getEventTypeInDifferentLanguages(event, locale, messageSource),
                        outputDate.format(inputDate.parse(event.getDate().toString())),
                        event.getTime().format(formatter),
                        event.getTime().plusMinutes(event.getEventType().getDuration()).format(formatter),
                        event.getSchool(),
                        event.getGroup(),
                        event.getGroupSize() == null ? "" : event.getGroupSize().toString(),
                        event.getTitleByLocale(locale),
                        event.getDescriptionByLocale(locale) == null ? "" : event.getDescriptionByLocale(locale).replace(",", ";"),
                };

                csvWriter.writeNext(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventType;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public interface DownloadService {
    void downloadCsv(String username, HttpServletResponse response,
                     HttpServletRequest request, Locale locale, String filename) throws IOException;

    void downloadPdf(String username, HttpServletResponse response,
                     HttpServletRequest request, Locale locale) throws IOException;

    List<Event> getEventsForDownloading(String username, HttpServletRequest request);

    List<EventType> extractEventTypes(List<String> eventTypesInfo);

}

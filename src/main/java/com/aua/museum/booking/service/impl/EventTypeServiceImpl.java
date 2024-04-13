package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.EventType;
import com.aua.museum.booking.exception.notfound.EventTypeNotFoundException;
import com.aua.museum.booking.locale.Language;
import com.aua.museum.booking.repository.EventTypeRepository;
import com.aua.museum.booking.service.EventTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventTypeServiceImpl implements EventTypeService {
    private final EventTypeRepository eventTypeRepository;

    @Override
    public EventType getEventTypeById(int id) {
        return eventTypeRepository.findById(id).orElseThrow(() -> new EventTypeNotFoundException(id));
    }

    @Override
    public List<EventType> getAllEventTypes() {
        return eventTypeRepository.findAll();
    }

    @Override
    public String getEventTypeValueFromLocale(EventType eventType) {
        final Locale locale = LocaleContextHolder.getLocale();
        final Language language = Language.valueOf(locale.getLanguage().toUpperCase());

        switch (language) {
            case EN:
                return eventType.getDisplayValue_EN();
            case RU:
                return eventType.getDisplayValue_RU();
            default:
                return eventType.getDisplayValue_AM();
        }
    }

    @Override
    public List<String> getAllEventTypeValues() {
        return getAllEventTypes().stream().map(this::getEventTypeValueFromLocale)
                .collect(Collectors.toList());
    }

    @Override
    public EventType getEventTypeByValue(String eventTypeValue) {
        return getAllEventTypes().stream()
                .filter(et -> eventTypeValue.equals(getEventTypeValueFromLocale(et)))
                .findFirst().orElseThrow(EventTypeNotFoundException::new);
    }
}
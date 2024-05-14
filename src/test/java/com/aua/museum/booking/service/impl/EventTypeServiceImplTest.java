package com.aua.museum.booking.service.impl;import com.aua.museum.booking.domain.EventType;import com.aua.museum.booking.exception.notfound.EventTypeNotFoundException;import com.aua.museum.booking.repository.EventTypeRepository;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.Mockito;import org.mockito.junit.jupiter.MockitoExtension;import org.springframework.context.i18n.LocaleContextHolder;import java.util.List;import java.util.Locale;import java.util.Optional;import static org.junit.jupiter.api.Assertions.assertEquals;import static org.junit.jupiter.api.Assertions.assertThrows;import static org.mockito.ArgumentMatchers.anyInt;import static org.mockito.BDDMockito.given;@ExtendWith(MockitoExtension.class)public class EventTypeServiceImplTest {    private final EventType EVENT_TYPE = new EventType();    private EventTypeRepository eventTypeRepository;    private EventTypeServiceImpl eventTypeService;    @BeforeEach    void setup() {        eventTypeRepository = Mockito.mock(EventTypeRepository.class);        eventTypeService = new EventTypeServiceImpl(eventTypeRepository);        EVENT_TYPE.setId(1);        EVENT_TYPE.setDuration(45);        EVENT_TYPE.setDisplayValueEN("Preschool");        EVENT_TYPE.setDisplayValueRU("Детский Сад");        EVENT_TYPE.setDisplayValueAM("Նախակրթական");    }    @Test    void getEventTypeById() {        given(eventTypeRepository.findById(anyInt())).willReturn(Optional.of(EVENT_TYPE));        assertEquals(EVENT_TYPE, eventTypeService.getEventTypeById(EVENT_TYPE.getId()));    }    @Test    void throwNotFoundWhenPassingNonExistingId() {        given(eventTypeRepository.findById(7)).willReturn(Optional.empty());        assertThrows(EventTypeNotFoundException.class, () -> eventTypeService.getEventTypeById(7));    }    @Test    void getAllEventTypes() {        List<EventType> list = List.of(EVENT_TYPE);        given(eventTypeRepository.findAll()).willReturn(list);        assertEquals(list, eventTypeService.getAllEventTypes());    }    @Test    void getEventTypeValueFromLocale() {        LocaleContextHolder.setLocale(new Locale("hy"));        String value = EVENT_TYPE.getDisplayValueAM();        assertEquals(value, eventTypeService.getEventTypeValueFromLocale(EVENT_TYPE));    }    @Test    void getAllEventTypeValues() {        LocaleContextHolder.setLocale(new Locale("hy"));        List<EventType> eventTypeList = List.of(EVENT_TYPE);        given(eventTypeService.getAllEventTypes()).willReturn(eventTypeList);        List<String> list = List.of(EVENT_TYPE.getDisplayValueAM());        List<String> actualList = eventTypeService.getAllEventTypeValues();        assertEquals(list, actualList);    }    @Test    void getEventTypeByValue() {        LocaleContextHolder.setLocale(new Locale("hy"));        List<EventType> eventTypeList = List.of(EVENT_TYPE);        given(eventTypeService.getAllEventTypes()).willReturn(eventTypeList);        EventType eventType = eventTypeService.getEventTypeByValue(EVENT_TYPE.getDisplayValueAM());        assertEquals(EVENT_TYPE, eventType);    }}
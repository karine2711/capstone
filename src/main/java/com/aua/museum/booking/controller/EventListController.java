package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventListController {

    private final EventService eventService;

    @GetMapping
    public ModelAndView viewPage(ModelAndView modelAndView) {

        List<Event> listEvents = eventService.getAllEvents();
        modelAndView.setViewName(Templates.EVENT_LIST.getName());
        modelAndView.addObject("listEvents", listEvents);
        return modelAndView;
    }
}

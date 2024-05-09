package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventLite;
import com.aua.museum.booking.domain.EventState;
import com.aua.museum.booking.domain.UserState;
import com.aua.museum.booking.service.CustomEventService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomEventServiceImpl implements CustomEventService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<EventLite> getAllWithoutPhoto() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EventLite> cq = builder.createQuery(EventLite.class);
        Root<Event> root = cq.from(Event.class);
        cq.multiselect(
                root.get("id"),
                root.get("user"),
                root.get("eventType"),
                root.get("title_AM"),
                root.get("title_EN"),
                root.get("title_RU"),
                root.get("date"),
                root.get("time"),
                root.get("school"),
                root.get("group"),
                root.get("groupSize"),
                root.get("description_AM"),
                root.get("description_RU"),
                root.get("description_EN"),
                root.get("eventState")
        );
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<EventLite> getActiveUsersPreBookedWithoutPhoto() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EventLite> cq = builder.createQuery(EventLite.class);
        Root<Event> root = cq.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("eventState"), EventState.PRE_BOOKED));
        Predicate predicateForActive = builder.equal(root.join("user").get("state"), UserState.ACTIVE);
        Predicate predicateForUnBlocked = builder.equal(root.join("user").get("state"), UserState.UNBLOCKED);
        Predicate predicateForWaitingList = builder.or(predicateForActive, predicateForUnBlocked);
        predicates.add(predicateForWaitingList);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.multiselect(
                root.get("id"),
                root.get("user"),
                root.get("eventType"),
                root.get("title_AM"),
                root.get("title_EN"),
                root.get("title_RU"),
                root.get("date"),
                root.get("time"),
                root.get("school"),
                root.get("group"),
                root.get("groupSize"),
                root.get("description_AM"),
                root.get("description_RU"),
                root.get("description_EN"),
                root.get("eventState")
        );
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<EventLite> getBlockedUsersPreBookedWithoutPhoto() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EventLite> cq = builder.createQuery(EventLite.class);
        Root<Event> root = cq.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("eventState"), EventState.PRE_BOOKED));
        predicates.add(builder.equal(root.join("user").get("state"), UserState.BLOCKED));
        cq.where(predicates.toArray(new Predicate[0]));
        cq.multiselect(
                root.get("id"),
                root.get("user"),
                root.get("eventType"),
                root.get("title_AM"),
                root.get("title_EN"),
                root.get("title_RU"),
                root.get("date"),
                root.get("time"),
                root.get("school"),
                root.get("group"),
                root.get("groupSize"),
                root.get("description_AM"),
                root.get("description_RU"),
                root.get("description_EN"),
                root.get("eventState")
        );
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<EventLite> getEventsWithTimes(LocalDate localDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EventLite> cq = cb.createQuery(EventLite.class);
        Root<Event> root = cq.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        if (localDate != null) {
            predicates.add(cb.equal(root.get("date"), localDate));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        cq.multiselect(
                root.get("id"),
                root.get("eventType"),
                root.get("date"),
                root.get("time"),
                root.get("groupSize")
        );
        cq.orderBy(cb.asc(root.get("time")));
        return em.createQuery(cq).getResultList();
    }
}

package com.travelapp.shared.exceptions;
import com.travelapp.events.domain.TravelEvent;
public class EventOverlapException extends RuntimeException {
    public EventOverlapException(TravelEvent conflicting) {
        super("Event overlaps with: " + conflicting.getTitle()
            + " (" + conflicting.getStartDatetime() + ")");
    }
}

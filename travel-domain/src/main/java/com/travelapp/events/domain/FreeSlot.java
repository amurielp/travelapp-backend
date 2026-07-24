package com.travelapp.events.domain;
import java.time.LocalTime;
public record FreeSlot(LocalTime from, LocalTime to) {}

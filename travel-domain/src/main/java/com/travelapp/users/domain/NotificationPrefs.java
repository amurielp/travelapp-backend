package com.travelapp.users.domain;
public record NotificationPrefs(boolean aiSuggestions, int budgetAlertPct, boolean weatherAlert, int[] beforeEventHours) {}

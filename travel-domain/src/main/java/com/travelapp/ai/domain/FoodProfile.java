package com.travelapp.ai.domain;
import java.util.List;
public record FoodProfile(String level, List<String> prefers, List<String> avoid, String diet, List<String> allergies) {}

package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceTokenRequest(
    @NotBlank String platform,
    @NotBlank String fcmToken,
    String deviceModel,
    String appVersion
) {}

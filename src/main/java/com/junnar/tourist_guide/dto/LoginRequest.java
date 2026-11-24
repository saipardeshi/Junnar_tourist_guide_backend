package com.junnar.tourist_guide.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email and password are required") String email,
        @NotBlank(message = "Email and password are required") String password
) {}

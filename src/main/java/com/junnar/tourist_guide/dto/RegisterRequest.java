package com.junnar.tourist_guide.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        @Size(max = 254, message = "Email must be at most 254 characters")
        String email,

        // BCrypt only uses the first 72 bytes, so longer passwords add nothing.
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters")
        String password
) {}

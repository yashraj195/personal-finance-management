package com.yash.finance.dto;

public record RegisterResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
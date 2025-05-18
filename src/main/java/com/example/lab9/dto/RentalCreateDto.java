package com.example.lab9.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RentalCreateDto {

    @NotNull(message = "DVD ID is required")
    private Long dvdId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Rental days is required")
    @Positive(message = "Rental days must be positive")
    private Integer rentalDays;
}
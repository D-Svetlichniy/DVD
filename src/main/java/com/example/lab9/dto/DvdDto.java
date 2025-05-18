package com.example.lab9.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class DvdDto extends BaseDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String director;

    @PastOrPresent(message = "Release year must be in the past or present")
    private LocalDate releaseDate;

    private String genre;

    @NotNull(message = "Rental rate is required")
    @Positive(message = "Rental rate must be positive")
    private BigDecimal rentalRatePerDay;

    private String description;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private Integer availableQuantity;
}
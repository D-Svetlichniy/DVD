package com.example.lab9.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class RentalDto extends BaseDto {

    @NotNull(message = "DVD ID is required")
    private Long dvdId;

    private String dvdTitle;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String userName;

    private LocalDate rentalDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private BigDecimal totalCost;

    private boolean isReturned;

    @Positive(message = "Rental days must be positive")
    private Integer rentalDays;
}
package com.example.lab9.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "dvds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dvd extends BaseEntity {

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    private String director;

    @PastOrPresent(message = "Release year must be in the past or present")
    @Column(name = "release_year")
    private LocalDate releaseDate;

    private String genre;

    @NotNull(message = "Rental rate is required")
    @Positive(message = "Rental rate must be positive")
    @Column(name = "rental_rate_per_day", nullable = false)
    private BigDecimal rentalRatePerDay;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer quantity;

    @Column(name = "available_quantity")
    private Integer availableQuantity;
}
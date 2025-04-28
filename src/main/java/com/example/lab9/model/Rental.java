package com.example.lab9.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental extends BaseEntity {

    @NotNull(message = "DVD is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dvd_id", nullable = false)
    private Dvd dvd;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Rental date is required")
    @PastOrPresent(message = "Rental date must be in the past or present")
    @Column(name = "rental_date", nullable = false)
    private LocalDate rentalDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @NotNull(message = "Total cost is required")
    @Positive(message = "Total cost must be positive")
    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "is_returned", nullable = false)
    private boolean isReturned = false;
}
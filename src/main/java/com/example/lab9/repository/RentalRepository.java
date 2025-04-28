package com.example.lab9.repository;

import com.example.lab9.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserIdOrderByRentalDateDesc(Long userId);

    List<Rental> findByDvdIdOrderByRentalDateDesc(Long dvdId);

    List<Rental> findByIsReturnedFalse();

    List<Rental> findByIsReturnedTrue();

    @Query("SELECT r FROM Rental r WHERE r.dueDate < CURRENT_DATE AND r.isReturned = false")
    List<Rental> findOverdueRentals();

    @Query("SELECT r FROM Rental r WHERE r.rentalDate >= :startDate AND r.rentalDate <= :endDate")
    List<Rental> findRentalsBetweenDates(LocalDate startDate, LocalDate endDate);
}
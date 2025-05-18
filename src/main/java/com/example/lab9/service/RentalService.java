package com.example.lab9.service;

import com.example.lab9.dto.RentalCreateDto;
import com.example.lab9.dto.RentalDto;

import java.time.LocalDate;
import java.util.List;

public interface RentalService extends BaseService<RentalDto, Long> {

    List<RentalDto> findByUserId(Long userId);

    List<RentalDto> findByDvdId(Long dvdId);

    List<RentalDto> findAllActive();

    List<RentalDto> findAllReturned();

    List<RentalDto> findOverdueRentals();

    List<RentalDto> findRentalsBetweenDates(LocalDate startDate, LocalDate endDate);

    RentalDto rentDvd(RentalCreateDto rentalCreateDto);

    RentalDto returnDvd(Long rentalId);
}
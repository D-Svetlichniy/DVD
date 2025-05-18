package com.example.lab9.service.impl;

import com.example.lab9.dto.RentalCreateDto;
import com.example.lab9.dto.RentalDto;
import com.example.lab9.exception.BusinessException;
import com.example.lab9.exception.ResourceNotFoundException;
import com.example.lab9.model.Dvd;
import com.example.lab9.model.Rental;
import com.example.lab9.model.User;
import com.example.lab9.repository.DvdRepository;
import com.example.lab9.repository.RentalRepository;
import com.example.lab9.repository.UserRepository;
import com.example.lab9.service.RentalService;
import com.example.lab9.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final DvdRepository dvdRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RentalServiceImpl(
            RentalRepository rentalRepository,
            DvdRepository dvdRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.rentalRepository = rentalRepository;
        this.dvdRepository = dvdRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findAll() {
        return rentalRepository.findAll().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RentalDto> findById(Long id) {
        return rentalRepository.findById(id)
                .map(objectMapper::toDto);
    }

    @Override
    public RentalDto save(RentalDto rentalDto) {
        Dvd dvd = dvdRepository.findById(rentalDto.getDvdId())
                .orElseThrow(() -> new ResourceNotFoundException("DVD", "id", rentalDto.getDvdId()));

        User user = userRepository.findById(rentalDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", rentalDto.getUserId()));

        Rental rental = objectMapper.toEntity(rentalDto, dvd, user);
        rental = rentalRepository.save(rental);

        return objectMapper.toDto(rental);
    }

    @Override
    public void deleteById(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rental", "id", id);
        }
        rentalRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findByUserId(Long userId) {
        return rentalRepository.findByUserIdOrderByRentalDateDesc(userId).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findByDvdId(Long dvdId) {
        return rentalRepository.findByDvdIdOrderByRentalDateDesc(dvdId).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findAllActive() {
        return rentalRepository.findByIsReturnedFalse().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findAllReturned() {
        return rentalRepository.findByIsReturnedTrue().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findOverdueRentals() {
        return rentalRepository.findOverdueRentals().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto> findRentalsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return rentalRepository.findRentalsBetweenDates(startDate, endDate).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto rentDvd(RentalCreateDto rentalCreateDto) {
        // Находим DVD
        Dvd dvd = dvdRepository.findById(rentalCreateDto.getDvdId())
                .orElseThrow(() -> new ResourceNotFoundException("DVD", "id", rentalCreateDto.getDvdId()));

        // Проверяем, доступен ли DVD для аренды
        if (dvd.getAvailableQuantity() <= 0) {
            throw new BusinessException("DVD '" + dvd.getTitle() + "' is not available for rent");
        }

        // Находим пользователя
        User user = userRepository.findById(rentalCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", rentalCreateDto.getUserId()));

        // Создаем объект аренды
        Rental rental = new Rental();
        rental.setDvd(dvd);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(rentalCreateDto.getRentalDays()));

        // Рассчитываем стоимость аренды
        BigDecimal totalCost = dvd.getRentalRatePerDay()
                .multiply(BigDecimal.valueOf(rentalCreateDto.getRentalDays()));
        rental.setTotalCost(totalCost);

        rental.setReturned(false);

        // Уменьшаем доступное количество DVD
        dvd.setAvailableQuantity(dvd.getAvailableQuantity() - 1);
        dvdRepository.save(dvd);

        // Сохраняем аренду
        rental = rentalRepository.save(rental);

        return objectMapper.toDto(rental);
    }

    @Override
    public RentalDto returnDvd(Long rentalId) {
        // Находим аренду
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", rentalId));

        // Проверяем, не возвращен ли уже диск
        if (rental.isReturned()) {
            throw new BusinessException("DVD has already been returned");
        }

        // Отмечаем диск как возвращенный
        rental.setReturned(true);
        rental.setReturnDate(LocalDate.now());

        // Увеличиваем доступное количество DVD
        Dvd dvd = rental.getDvd();
        dvd.setAvailableQuantity(dvd.getAvailableQuantity() + 1);
        dvdRepository.save(dvd);

        // Сохраняем аренду
        rental = rentalRepository.save(rental);

        return objectMapper.toDto(rental);
    }
}
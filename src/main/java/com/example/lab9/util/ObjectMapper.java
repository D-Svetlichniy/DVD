package com.example.lab9.util;

import com.example.lab9.dto.DvdDto;
import com.example.lab9.dto.RentalDto;
import com.example.lab9.dto.UserDto;
import com.example.lab9.model.Dvd;
import com.example.lab9.model.Rental;
import com.example.lab9.model.User;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapper {

    public DvdDto toDto(Dvd entity) {
        if (entity == null) {
            return null;
        }

        DvdDto dto = new DvdDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDirector(entity.getDirector());
        dto.setReleaseDate(entity.getReleaseDate());
        dto.setGenre(entity.getGenre());
        dto.setRentalRatePerDay(entity.getRentalRatePerDay());
        dto.setDescription(entity.getDescription());
        dto.setQuantity(entity.getQuantity());
        dto.setAvailableQuantity(entity.getAvailableQuantity());

        return dto;
    }

    public Dvd toEntity(DvdDto dto) {
        if (dto == null) {
            return null;
        }

        Dvd entity = new Dvd();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDirector(dto.getDirector());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setGenre(dto.getGenre());
        entity.setRentalRatePerDay(dto.getRentalRatePerDay());
        entity.setDescription(dto.getDescription());
        entity.setQuantity(dto.getQuantity());
        entity.setAvailableQuantity(dto.getAvailableQuantity());

        return entity;
    }

    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());

        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());

        return entity;
    }

    public RentalDto toDto(Rental entity) {
        if (entity == null) {
            return null;
        }

        RentalDto dto = new RentalDto();
        dto.setId(entity.getId());

        if (entity.getDvd() != null) {
            dto.setDvdId(entity.getDvd().getId());
            dto.setDvdTitle(entity.getDvd().getTitle());
        }

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getName());
        }

        dto.setRentalDate(entity.getRentalDate());
        dto.setDueDate(entity.getDueDate());
        dto.setReturnDate(entity.getReturnDate());
        dto.setTotalCost(entity.getTotalCost());
        dto.setReturned(entity.isReturned());

        return dto;
    }

    public Rental toEntity(RentalDto dto, Dvd dvd, User user) {
        if (dto == null) {
            return null;
        }

        Rental entity = new Rental();
        entity.setId(dto.getId());
        entity.setDvd(dvd);
        entity.setUser(user);
        entity.setRentalDate(dto.getRentalDate());
        entity.setDueDate(dto.getDueDate());
        entity.setReturnDate(dto.getReturnDate());
        entity.setTotalCost(dto.getTotalCost());
        entity.setReturned(dto.isReturned());

        return entity;
    }
}
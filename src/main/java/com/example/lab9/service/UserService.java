package com.example.lab9.service;

import com.example.dvdrentalservice.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<UserDto, Long> {

    Optional<UserDto> findByEmail(String email);

    List<UserDto> findByNameContaining(String name);

    boolean existsByEmail(String email);
}
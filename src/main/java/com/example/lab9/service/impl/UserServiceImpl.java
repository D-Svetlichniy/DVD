package com.example.lab9.service.impl;

import com.example.lab9.dto.UserDto;
import com.example.lab9.exception.BusinessException;
import com.example.lab9.exception.ResourceNotFoundException;
import com.example.lab9.model.User;
import com.example.lab9.repository.UserRepository;
import com.example.lab9.service.UserService;
import com.example.lab9.util.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
                .map(objectMapper::toDto);
    }

    @Override
    public UserDto save(UserDto userDto) {
        // Проверяем, существует ли пользователь с таким email (кроме текущего пользователя при обновлении)
        if (userDto.getId() == null) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new BusinessException("User with email " + userDto.getEmail() + " already exists");
            }
        } else {
            userRepository.findByEmail(userDto.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userDto.getId())) {
                            throw new BusinessException("User with email " + userDto.getEmail() + " already exists");
                        }
                    });
        }

        User user = objectMapper.toEntity(userDto);
        user = userRepository.save(user);
        return objectMapper.toDto(user);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(objectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByNameContaining(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(objectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
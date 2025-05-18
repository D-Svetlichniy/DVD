package com.example.lab9.service;

import com.example.lab9.dto.UserDto;
import com.example.lab9.exception.BusinessException;
import com.example.lab9.model.User;
import com.example.lab9.repository.UserRepository;
import com.example.lab9.service.impl.UserServiceImpl;
import com.example.lab9.util.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhone("+380501234567");
        user.setAddress("123 Main St");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setPhone("+380501234567");
        userDto.setAddress("123 Main St");
    }

    @Test
    public void testFindAll() {
        // Arrange
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(objectMapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        List<UserDto> result = userService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(objectMapper.toDto(user)).thenReturn(userDto);

        // Act
        Optional<UserDto> result = userService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testSaveNewUser() {
        // Arrange
        userDto.setId(null);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(objectMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(objectMapper.toDto(user)).thenReturn(userDto);

        // Act
        UserDto result = userService.save(userDto);

        // Assert
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSaveNewUserWithExistingEmail() {
        // Arrange
        userDto.setId(null);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.save(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testSaveExistingUser() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));
        when(objectMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(objectMapper.toDto(user)).thenReturn(userDto);

        // Act
        UserDto result = userService.save(userDto);

        // Assert
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSaveExistingUserWithDifferentEmail() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.save(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(objectMapper.toDto(user)).thenReturn(userDto);

        // Act
        Optional<UserDto> result = userService.findByEmail("john@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    public void testFindByNameContaining() {
        // Arrange
        List<User> users = Arrays.asList(user);
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(users);
        when(objectMapper.toDto(any(User.class))).thenReturn(userDto);

        // Act
        List<UserDto> result = userService.findByNameContaining("John");

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(userRepository, times(1)).findByNameContainingIgnoreCase("John");
    }
}
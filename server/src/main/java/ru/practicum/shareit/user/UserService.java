package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Запрос на создание юзера");
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Отсутствует email.");
        }
        try {
            User userToAdd = UserMapper.mapFrom(userDto);
            userRepository.save(userToAdd);
            return UserMapper.mapTo(userToAdd);
        } catch (RuntimeException e) {
            throw new DuplicateEmailException("Юзер с такой почтой уже существует");
        }
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Запрос на обновление юзера");
        if (getUserById(id) != null) {
            User updatedUser = validateUserForUpdate(id, UserMapper.mapFrom(userDto));
            userRepository.save(updatedUser);
            return UserMapper.mapTo(updatedUser);
        } else {
            throw new NotFoundException("Нет такого юзера.");
        }
    }

    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение всех юзеров");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapTo)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        log.info("Запрос на получение юзера");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден такой юзер"));
        return UserMapper.mapTo(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.info("Запрос на удаление юзера");
        try {
            userRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Не найден такой юзер");
        }
    }

    private User validateUserForUpdate(Long userId, User user) {
        User userToUpdate = UserMapper.mapFrom(getUserById(userId));
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }
}

package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        log.info("Запрос на создание юзера");
        return userRepository.createUser(userDto);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Запрос на обновление юзера");
        return userRepository.updateUser(id, userDto);
    }

    public Set<UserDto> getAllUsers() {
        log.info("Запрос на получение всех юзеров");
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(Long id) {
        log.info("Запрос на получение юзера");
        return userRepository.getUserById(id);
    }

    public void deleteUserById(Long id) {
        log.info("Запрос на удаление юзера");
        userRepository.deleteUserById(id);
    }
}

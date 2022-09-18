package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class UserRepository {
    private final Set<String> emails = new HashSet<>();
    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper userMapper = new UserMapper();
    private Long id = 1L;

    public UserDto createUser(UserDto userDto) {
        User userToAdd = userMapper.mapFrom(userDto);
        if (!emails.contains(userToAdd.getEmail())) {
            userToAdd.setId(id);
            users.put(userToAdd.getId(), userToAdd);
            emails.add(userToAdd.getEmail());
            userDto = userMapper.mapTo(users.get(id));
            id++;
            return userDto;
        } else {
            throw new DuplicateEmailException("Такая почта уже используется");
        }
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = userMapper.mapFrom(userDto);
        if (users.containsKey(id)) {
            if (userToUpdate.getEmail() != null && emails.contains(userToUpdate.getEmail())) {
                throw new DuplicateEmailException("Замена почты на уже существующую");
            } else if (userToUpdate.getName() == null
                    && userToUpdate.getEmail() != null) {
                userToUpdate.setName(users.get(id).getName());
                userToUpdate.setId(id);
                emails.remove(users.get(id).getEmail());
                emails.add(userToUpdate.getEmail());
                users.put(id, userToUpdate);
            } else if (userToUpdate.getEmail() == null
                    && userToUpdate.getName() != null) {
                userToUpdate.setEmail(users.get(id).getEmail());
                userToUpdate.setId(id);
                users.put(id, userToUpdate);
            } else if (userToUpdate.getEmail() != null
                    && userToUpdate.getName() != null) {
                userToUpdate.setId(id);
                emails.remove(users.get(id).getEmail());
                emails.add(userToUpdate.getEmail());
                users.put(id, userToUpdate);
            } else {
                throw new ValidationException("Некорректный запрос на обновление пользователя");
            }
            userDto = userMapper.mapTo(users.get(id));
        } else {
            throw new NotFoundException("Нет юзера с таким айди");
        }
        return userDto;
    }

    public UserDto getUserById(Long id) {
        try {
            if (users.containsKey(id)) {
                return userMapper.mapTo(users.get(id));
            }
        } catch (RuntimeException e) {
            throw new NotFoundException("Не найден юзер с данным id");
        }
        return null;
    }

    public Set<UserDto> getAllUsers() {
        Set<UserDto> userDtos = new HashSet<>();
        for (User user : users.values()) {
            userDtos.add(userMapper.mapTo(user));
        }
        return userDtos;
    }

    public void deleteUserById(Long id) {
        try {
            emails.remove(users.get(id).getEmail());
            users.remove(id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Не найден юзер с данным id");
        }
    }

}

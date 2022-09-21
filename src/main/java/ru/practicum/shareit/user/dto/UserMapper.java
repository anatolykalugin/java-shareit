package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.Mapper;
import ru.practicum.shareit.user.model.User;

public class UserMapper implements Mapper<UserDto, User> {
    @Override
    public UserDto mapTo(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public User mapFrom(UserDto userDto) {
        return new User(
                0L,
                userDto.getName(),
                userDto.getEmail()
        );
    }
}

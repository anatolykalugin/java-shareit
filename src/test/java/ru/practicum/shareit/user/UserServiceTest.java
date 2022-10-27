package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final User user1 = new User(1L, "Denis", "denis@mail.ru");
    private final User user2 = new User(2L, "Dima", "dima@mail.ru");
    private final User user3 = new User(3L, "Anton", "anton@mail.ru");

    @Autowired
    public UserServiceTest(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @Test
    void shouldCreateUser() {
        UserDto newUser = userService.createUser(UserMapper.mapTo(user3));
        assertEquals(userService.getUserById(newUser.getId()).getId(), user3.getId());
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = userService.getUserById(1L);
        userDto.setName("Svetlana");
        userDto.setEmail("svetlana@mail.ru");
        userService.updateUser(1L, userDto);
        assertEquals("Svetlana", userService.getUserById(1L).getName());
        assertEquals("svetlana@mail.ru", userService.getUserById(1L).getEmail());
    }

    @Test
    void shouldGetUserById() {
        assertEquals(UserMapper.mapTo(user1).getId(), userService.getUserById(1L).getId());
    }

    @Test
    void shouldGetAllUsers() {
        assertEquals(3, userService.getAllUsers().size());
    }

    @Test
    void shouldFailCreatingNewUserNullEmail() {
        User user4 = new User(4L, "Igor", null);
        assertThrows(ValidationException.class, () -> userService.createUser(UserMapper.mapTo(user4)));
    }

    @Test
    void shouldFailCreatingNewUserDupeEmail() {
        User user5 = new User(4L, "Igor", "dima@mail.ru");
        assertThrows(DuplicateEmailException.class, () -> userService.createUser(UserMapper.mapTo(user5)));
    }

    @Test
    void shouldFailUpdatingUserWrongUser() {
        UserDto user6 = new UserDto(4L, "Igor", "dima2@mail.ru");
        assertThrows(NotFoundException.class, () -> userService.updateUser(-1L, user6));
    }

    @Test
    void shouldDeleteUserById() {
        userService.deleteUserById(1L);
        assertNull(userRepository.findById(1L).orElse(null));
    }

    @Test
    void shouldFailDeleteUserById() {
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(6L));
    }
}

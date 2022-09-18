package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    @NotNull(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    private String email;
}

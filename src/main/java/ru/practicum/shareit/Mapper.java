package ru.practicum.shareit;

public interface Mapper<T, S> {
    T mapTo(S s);
    S mapFrom(T t);
}

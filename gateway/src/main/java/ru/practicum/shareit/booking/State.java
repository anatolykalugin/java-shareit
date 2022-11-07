package ru.practicum.shareit.booking;

import java.util.Optional;

public enum State {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static Optional<State> findState(String stateToFind) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stateToFind)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

}

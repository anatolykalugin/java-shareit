DROP schema public cascade;
create schema public;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(200),
    requestor   BIGINT REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50),
    description VARCHAR(200),
    available   BOOLEAN,
    owner       BIGINT REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item       BIGINT REFERENCES items (id),
    booker     BIGINT REFERENCES users (id),
    status     VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS comments
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    texts   VARCHAR(500),
    item   BIGINT REFERENCES items (id),
    author BIGINT REFERENCES users (id),
    created TIMESTAMP WITHOUT TIME ZONE
);
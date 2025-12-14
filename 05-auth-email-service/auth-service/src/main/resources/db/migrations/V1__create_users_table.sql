CREATE TABLE if not exists users
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    password       VARCHAR(254) NOT NULL,
    email          VARCHAR      NOT NULL UNIQUE
);

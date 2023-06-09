DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS rating_mpa;


CREATE TABLE IF NOT EXISTS users (
user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
login VARCHAR(100) NOT NULL,
name VARCHAR(100),
email VARCHAR(100) NOT NULL,
birthday DATE
);

CREATE TABLE IF NOT EXISTS friends (
user_id BIGINT REFERENCES users(user_id),
friend_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS rating_mpa (
mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS films (
film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(100) NOT NULL,
description VARCHAR(200),
release_date DATE,
duration INTEGER,
mpa_id INTEGER REFERENCES rating_mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
film_id BIGINT REFERENCES films(film_id),
user_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS genre (
genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS film_genre (
film_id BIGINT REFERENCES films(film_id),
genre_id INTEGER REFERENCES genre(genre_id)
);









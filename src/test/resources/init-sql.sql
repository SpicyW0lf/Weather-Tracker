CREATE TABLE IF NOT EXISTS users
(
    id       serial PRIMARY KEY not null,
    username varchar(255)       NOT NULL UNIQUE,
    password varchar(255)       NOT NULL
);

create table if not exists locations
(
    id        serial primary key  not null,
    name      varchar(255) unique not null,
    latitude  double precision    not null,
    longitude double precision    not null,
    country   varchar(255)
);

create table if not exists userlocs
(
    user_id     integer not null references users (id),
    location_id integer not null references locations (id),
    primary key (user_id, location_id),
    unique (user_id, location_id)
);
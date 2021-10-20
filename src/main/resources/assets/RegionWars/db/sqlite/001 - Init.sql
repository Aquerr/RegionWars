CREATE TABLE version (
    version INT NOT NULL
);

CREATE TABLE region (
    name TEXT PRIMARY KEY NOT NULL,
    world_uuid TEXT NOT NULL,
    first_position TEXT NOT NULL,
    second_position TEXT NOT NULL,
    active INT NOT NULL DEFAULT '0'
);

INSERT INTO version (version) VALUES (1);
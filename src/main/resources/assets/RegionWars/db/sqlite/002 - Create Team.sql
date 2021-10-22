CREATE TABLE team (
    name TEXT PRIMARY KEY NOT NULL
);

CREATE TABLE team_member (
    team_name TEXT NOT NULL,
    player_uuid TEXT NOT NULL,
    FOREIGN KEY(team_name) REFERENCES team(name)
);

INSERT INTO version (version) VALUES (2);
package io.github.aquerr.regionwars.storage;

import io.github.aquerr.regionwars.model.Team;

import java.util.List;

public interface TeamStorage
{
    List<Team> getTeams();

    Team getTeam(String name);

    void saveTeam(Team team);

    void deleteTeam(String name);
}

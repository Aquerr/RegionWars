package io.github.aquerr.regionwars.service;

import io.github.aquerr.regionwars.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService
{
    List<Team> getTeams();

    Optional<Team> getTeam(String name);

    void saveTeam(Team team);

    void deleteTeam(String name);
}

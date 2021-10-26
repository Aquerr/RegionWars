package io.github.aquerr.regionwars.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.aquerr.regionwars.exception.TeamNotFoundException;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.storage.TeamStorage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TeamServiceImpl implements TeamService
{
    private final TeamStorage teamStorage;
    private final LoadingCache<String, Team> nameTeamCache;

    public TeamServiceImpl(TeamStorage teamStorage)
    {
        this.teamStorage = teamStorage;
        this.nameTeamCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>()
                {
                    @Override
                    public Team load(String key) throws Exception
                    {
                        return Optional.ofNullable(teamStorage.getTeam(key)).orElseThrow(() -> new TeamNotFoundException("No such team in db."));
                    }
                });

        this.teamStorage.getTeams().forEach(team -> this.nameTeamCache.put(team.getName().toLowerCase(), team));
    }

    @Override
    public List<Team> getTeams()
    {
        return List.copyOf(this.nameTeamCache.asMap().values());
    }

    @Override
    public Optional<Team> getTeam(String name)
    {
        Team team = null;
        try
        {
            team = this.nameTeamCache.get(name.toLowerCase());
        }
        catch (ExecutionException e)
        {
            if (!(e.getCause() instanceof TeamNotFoundException))
                e.printStackTrace();
        }
        return Optional.ofNullable(team);
    }

    @Override
    public void saveTeam(Team team)
    {
        this.nameTeamCache.put(team.getName().toLowerCase(), team);
        CompletableFuture.runAsync(() -> this.teamStorage.saveTeam(team));
    }

    @Override
    public void deleteTeam(String name)
    {
        Team team = this.nameTeamCache.getIfPresent(name.toLowerCase());

        if (team != null)
        {
            this.nameTeamCache.invalidate(name.toLowerCase());
        }

        CompletableFuture.runAsync(() -> this.teamStorage.deleteTeam(name));
    }

    @Override
    public Optional<Team> getTeamForPlayer(Player player)
    {
        return getTeams().stream()
                .filter(team -> team.getMembers().contains(player.getUniqueId()))
                .findFirst();
    }
}

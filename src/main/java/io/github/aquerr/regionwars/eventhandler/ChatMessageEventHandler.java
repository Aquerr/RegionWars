package io.github.aquerr.regionwars.eventhandler;

import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatMessageEventHandler implements Listener
{
    private final TeamService teamService;

    public ChatMessageEventHandler(final TeamService teamService)
    {
        this.teamService = teamService;
    }

    @EventHandler
    public void onChatMessage(final AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        String messageFormat = event.getFormat();
        Optional<Team> optionalTeam = this.teamService.getTeamForPlayer(player);
        if (optionalTeam.isPresent())
        {
            messageFormat = addTeamPrefix(messageFormat, optionalTeam.get());
        }
        event.setFormat(messageFormat);
    }

    private String addTeamPrefix(String messageFormat, Team team)
    {
        return "[" + team.getColor() + team.getName() + ChatColor.RESET + "]" + messageFormat;
    }
}

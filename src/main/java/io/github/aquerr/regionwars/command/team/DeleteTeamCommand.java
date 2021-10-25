package io.github.aquerr.regionwars.command.team;

import io.github.aquerr.regionwars.PluginPermissions;
import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.command.RegionWarsCommand;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DeleteTeamCommand extends RegionWarsCommand
{
    private final TeamService teamService;

    public DeleteTeamCommand(final TeamService teamService)
    {
        super("delete_team",
                Set.of("delete_team"),
                "Deletes the specified team",
                PluginPermissions.DELETE_TEAM_COMMAND,
                "/rw delete_team",
                null);
        this.teamService = teamService;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments) throws CommandException
    {
        if (arguments.length != 2)
            throw new CommandException("You need to provide exactly one team name!");

        String teamName = arguments[1];
        if (this.teamService.getTeam(teamName).isEmpty())
            throw new CommandException("Team with such name does not exist!");

        this.teamService.deleteTeam(teamName);
        commandSender.spigot().sendMessage(new ComponentBuilder()
                .append(RegionWarsPlugin.PLUGIN_PREFIX)
                .append("Team " + teamName + " has been deleted!")
                .color(ChatColor.GREEN)
                .create());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        if (arguments.length > 1)
        {
            return this.teamService.getTeams().stream()
                    .map(Team::getName)
                    .filter(teamName -> StringUtil.startsWithIgnoreCase(teamName, arguments[1]))
                    .toList();
        }
        else
        {
            return Collections.emptyList();
        }
    }
}

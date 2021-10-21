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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CreateTeamCommand extends RegionWarsCommand
{
    private final TeamService teamService;

    public CreateTeamCommand(TeamService teamService)
    {
        super("create_team",
                Set.of("create_team"),
                "Creates a region wars team",
                PluginPermissions.CREATE_TEAM_COMMAND,
                "/rw create_team <name>",
                null);
        this.teamService = teamService;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments) throws CommandException
    {
        String teamName;
        if (arguments.length > 1)
        {
            teamName = arguments[1];
        }
        else
        {
            throw new CommandException("You need to provide team's name!");
        }

        // Check if team already exists
        if (teamService.getTeam(teamName).isPresent())
            throw new CommandException("This name is already occupied by another team!");

        this.teamService.saveTeam(new Team(teamName));
        commandSender.spigot().sendMessage(new ComponentBuilder().append(RegionWarsPlugin.PLUGIN_PREFIX).append("Team has been created!").color(ChatColor.GREEN).create());

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        return Collections.emptyList();
    }
}

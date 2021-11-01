package io.github.aquerr.regionwars.command.team;

import io.github.aquerr.regionwars.PluginPermissions;
import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.command.RegionWarsCommand;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class RemoveTeamMemberCommand extends RegionWarsCommand
{
    private final TeamService teamService;

    public RemoveTeamMemberCommand(TeamService teamService)
    {
        super("remove_member",
                Set.of("remove_member"),
                "Removes player from the selected team",
                PluginPermissions.REMOVE_TEAM_MEMBER_COMMAND,
                "/rw team <team> remove_member <player>",
                null);
        this.teamService = teamService;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments) throws CommandException
    {
        if (arguments.length != 4)
            throw new CommandException("You need to provide player's name!");

        final Player player = Bukkit.getServer().getPlayer(arguments[3]);
        if (player == null)
            throw new CommandException("There is no player called '" + arguments[3] + "'");

        final Team team = this.teamService.getTeam(arguments[1])
                .orElseThrow(() -> new CommandException("Team with such name does not exist!"));

        if (this.teamService.getTeamForPlayer(player).isEmpty())
            throw new CommandException("Given player is not in a team!");

        team.getMembers().remove(player.getUniqueId());
        this.teamService.saveTeam(team);
        commandSender.spigot().sendMessage(new ComponentBuilder()
                .append(RegionWarsPlugin.PLUGIN_PREFIX)
                .append("Player " + player.getName() + " has been removed from the team " + team.getName() + "!")
                .color(ChatColor.GREEN)
                .create());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        if (arguments.length == 4)
        {
            String teamName = arguments[1];
            return this.teamService.getTeam(teamName)
                    .map(team -> team.getMembers().stream())
                    .map(uuid -> uuid.map(Bukkit::getOfflinePlayer))
                    .map(player -> player.map(OfflinePlayer::getName))
                    .map(Stream::toList)
                    .orElse(Collections.emptyList());
        }
        else
        {
            return Bukkit.getOnlinePlayers().stream()
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }
}

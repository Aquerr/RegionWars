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
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AddTeamMemberCommand extends RegionWarsCommand
{
    private final TeamService teamService;

    public AddTeamMemberCommand(TeamService teamService)
    {
        super("add_member",
                Set.of("add_member"),
                "Adds player to the selected team",
                PluginPermissions.ADD_TEAM_MEMBER_COMMAND,
                "/rw team <team> add_member <player>",
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

        if (this.teamService.getTeamForPlayer(player).isPresent())
            throw new CommandException("Given player is already in a team!");

        team.getMembers().add(player.getUniqueId());
        this.teamService.saveTeam(team);
        commandSender.spigot().sendMessage(new ComponentBuilder()
                .append(RegionWarsPlugin.PLUGIN_PREFIX)
                .append("Player " + player.getName() + " has been added to the team " + team.getName() + "!")
                .color(ChatColor.GREEN)
                .create());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        if (arguments.length == 4)
        {
            return Bukkit.getOnlinePlayers().stream()
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, arguments[3]))
                    .toList();
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

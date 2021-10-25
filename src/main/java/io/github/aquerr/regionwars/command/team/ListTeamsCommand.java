package io.github.aquerr.regionwars.command.team;

import com.google.common.collect.ObjectArrays;
import io.github.aquerr.regionwars.PluginPermissions;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.command.RegionWarsCommand;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public class ListTeamsCommand extends RegionWarsCommand
{
    private final TeamService teamService;

    public ListTeamsCommand(TeamService teamService)
    {
        super("list_teams",
                Set.of("list_teams"),
                "Lists all teams",
                PluginPermissions.LIST_TEAMS_COMMAND,
                "/rw list_teams",
                null);
        this.teamService = teamService;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments) throws CommandException
    {
        int page = 1;
        // First param == "help"
        if (arguments.length > 1)
        {
            page = Integer.parseInt(arguments[1]);
        }

        final List<Team> teams = this.teamService.getTeams();

        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append("==========").append(" Teams List ").color(ChatColor.GOLD).append("==========").color(ChatColor.RESET).append("\n");

        int rows = 0;
        int totalpages = 1;
        final Map<Integer, BaseComponent[]> pages = new TreeMap<>();
        pages.put(totalpages, new ComponentBuilder().append("").create()); // Initial empty page
        for (final Team team : teams)
        {
            rows++;

            TextComponent dashComponent = new TextComponent("- ");
            TextComponent teamComponent = new TextComponent(team.getName());

            List<String> playerNames = team.getMembers().stream()
                    .map(playerUUID -> Optional.ofNullable(Bukkit.getServer().getPlayer(playerUUID)).map(HumanEntity::getName).orElse(playerUUID.toString()))
                    .toList();

            BaseComponent[] teamHoverTextComponents = new ComponentBuilder()
                    .append("Members: ").color(ChatColor.GOLD)
                    .append(String.join(",", playerNames)).color(ChatColor.WHITE)
                    .create();

            teamComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(teamHoverTextComponents)));
            teamComponent.setColor(ChatColor.GOLD);
            ComponentBuilder teamComponentBuilder = new ComponentBuilder();
            teamComponentBuilder.append(dashComponent).append(teamComponent).append("\n");
            BaseComponent[] teamBaseComponents = teamComponentBuilder.create();

            pages.merge(totalpages, teamBaseComponents, (baseComponents, baseComponents2) -> ObjectArrays.concat(baseComponents, baseComponents2, BaseComponent.class));

            if (rows == 6)
            {
                rows = 0;
                totalpages++;
            }
        }

        componentBuilder.append(pages.get(page));

        final TextComponent previousPageTextComponent = new TextComponent("<");
        previousPageTextComponent.setUnderlined(true);
        previousPageTextComponent.setColor(ChatColor.BLUE);
        previousPageTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rw list_teams " + (page == 1 ? page : --page)));
        final TextComponent nextPageTextComponent = new TextComponent(">");
        nextPageTextComponent.setUnderlined(true);
        nextPageTextComponent.setColor(ChatColor.BLUE);
        nextPageTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rw list_teams " + (page == totalpages ? page : ++page)));
        componentBuilder.append(new TextComponent("=============="), ComponentBuilder.FormatRetention.NONE).append(previousPageTextComponent).append(" " + page + " ").underlined(false).append(nextPageTextComponent).append("==============").underlined(false).color(ChatColor.RESET);

        commandSender.spigot().sendMessage(componentBuilder.create());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        return Collections.emptyList();
    }
}

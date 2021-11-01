package io.github.aquerr.regionwars.command.team;

import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.command.RegionWarsCommand;
import io.github.aquerr.regionwars.model.Team;
import io.github.aquerr.regionwars.service.TeamService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TeamCommands extends RegionWarsCommand
{
    private final TeamService teamService;

    public TeamCommands(TeamService teamService)
    {
        super("team",
                Set.of("team"),
                "Team commands",
                "",
                "/rw team <team>",
                Map.of(Set.of("add_member"), new AddTeamMemberCommand(teamService),
                        Set.of("remove_member"), new RemoveTeamMemberCommand(teamService)));
        this.teamService = teamService;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments) throws CommandException
    {
        if(arguments.length > 2)
        {
            String subCommandAlias = arguments[2];
            Optional<RegionWarsCommand> optionalRegionWarsCommand = childCommands.entrySet().stream()
                    .filter(setRegionWarsCommandEntry -> setRegionWarsCommandEntry.getKey().contains(subCommandAlias))
                    .map(Map.Entry::getValue)
                    .findFirst();
            if (optionalRegionWarsCommand.isEmpty())
                return false;

            RegionWarsCommand regionWarsCommand = optionalRegionWarsCommand.get();
            if (!regionWarsCommand.hasPermission(commandSender))
                return false;

            return regionWarsCommand.execute(commandSender, arguments);
        }

        int page = 1;
        final Collection<RegionWarsCommand> commands = super.childCommands.values();

        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append("==========").append(" Region Wars Commands ").color(ChatColor.GOLD).append("==========").color(ChatColor.RESET).append("\n");

        StringBuilder stringBuilder = new StringBuilder();
        for (final RegionWarsCommand command : commands)
        {
            stringBuilder.append("-")
                    .append(" ")
                    .append(ChatColor.GOLD)
                    .append(command.getUsageMessage())
                    .append(" ")
                    .append(ChatColor.RESET)
                    .append("-")
                    .append(" ")
                    .append(command.getDescription())
                    .append("\n");
        }

        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(stringBuilder.toString(), page, ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH, ChatPaginator.OPEN_CHAT_PAGE_HEIGHT - 2);

        componentBuilder.append(String.join("\n", chatPage.getLines()));
        componentBuilder.append("\n");

        final TextComponent previousPageTextComponent = new TextComponent("<");
        previousPageTextComponent.setUnderlined(true);
        previousPageTextComponent.setColor(ChatColor.BLUE);
        previousPageTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rw help " + (page == 1 ? page : --page)));
        final TextComponent nextPageTextComponent = new TextComponent(">");
        nextPageTextComponent.setUnderlined(true);
        nextPageTextComponent.setColor(ChatColor.BLUE);
        nextPageTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rw help " + (page == chatPage.getTotalPages() ? page : ++page)));
        componentBuilder.append("==================").append(previousPageTextComponent).append(" " + page + " ").underlined(false).append(nextPageTextComponent).append("==================").underlined(false).color(ChatColor.RESET);

        commandSender.spigot().sendMessage(componentBuilder.create());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        if (arguments.length == 2)
        {
            return this.teamService.getTeams().stream()
                    .map(Team::getName)
                    .filter(teamName -> StringUtil.startsWithIgnoreCase(teamName, arguments[1]))
                    .toList();
        }
        else if (arguments.length > 3)
        {
            return this.childCommands.entrySet().stream()
                    .filter(childCommandEntry -> childCommandEntry.getKey().contains(arguments[2]))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .map(childCommandEntry -> childCommandEntry.tabComplete(commandSender, arguments))
                    .orElse(Collections.emptyList());
        }
        else
        {
            return childCommands.keySet().stream()
                    .flatMap(Collection::stream)
                    .distinct()
                    .toList();
        }
    }
}

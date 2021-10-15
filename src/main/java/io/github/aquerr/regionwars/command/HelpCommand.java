package io.github.aquerr.regionwars.command;

import io.github.aquerr.regionwars.PluginPermissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HelpCommand extends RegionWarsCommand
{
    public HelpCommand()
    {
        super("help",
                Set.of("help"),
                "Displays all available commands",
                PluginPermissions.HELP_COMMAND,
                "/rw help",
                null);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments)
    {
        int page = 1;
        // First param == "help"
        if (arguments.length > 1)
        {
            page = Integer.parseInt(arguments[1]);
        }

        final List<RegionWarsCommand> commands = RegionWarsCommandsList.getCommandList().getCommands();

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
        return Collections.emptyList();
    }
}

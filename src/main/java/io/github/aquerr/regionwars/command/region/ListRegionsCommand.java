package io.github.aquerr.regionwars.command.region;

import io.github.aquerr.regionwars.PluginPermissions;
import io.github.aquerr.regionwars.command.CommandException;
import io.github.aquerr.regionwars.command.RegionWarsCommand;
import io.github.aquerr.regionwars.model.Region;
import io.github.aquerr.regionwars.service.RegionService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ListRegionsCommand extends RegionWarsCommand
{
    private final RegionService regionService;

    public ListRegionsCommand(RegionService regionService)
    {
        super("list_regions",
                Set.of("list_regions"),
                "Lists all regions",
                PluginPermissions.LIST_REGIONS_COMMAND,
                "/rw list_regions",
                null);
        this.regionService = regionService;
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

        final List<Region> regions = this.regionService.getRegions();

        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append("==========").append(" Regions List ").color(ChatColor.GOLD).append("==========").color(ChatColor.RESET).append("\n");

        int rows = 0;
        int totalpages = 1;
        final Map<Integer, List<BaseComponent>> pages = new TreeMap<>();
        for (final Region region : regions)
        {
            rows++;

            TextComponent dashComponent = new TextComponent("- ");
            TextComponent regionComponent = new TextComponent(region.getName());

            BaseComponent[] regionHoverTextComponents = new ComponentBuilder()
                    .append("World UUID: ").color(ChatColor.GOLD)
                    .append(region.getWorldUUID().toString() + "\n").color(ChatColor.WHITE)
                    .append("First Position: ").color(ChatColor.GOLD)
                    .append(region.getFirstPosition().toString() + "\n").color(ChatColor.WHITE)
                    .append("Second Position: ").color(ChatColor.GOLD)
                    .append(region.getSecondPosition().toString()).color(ChatColor.WHITE)
                    .create();

            regionComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(regionHoverTextComponents)));
            regionComponent.setColor(ChatColor.GOLD);
            ComponentBuilder regionComponentBuilder = new ComponentBuilder();
            regionComponentBuilder.append(dashComponent).append(regionComponent).append("\n");
            BaseComponent[] regionBaseComponents = regionComponentBuilder.create();

            pages.merge(totalpages, Arrays.asList(regionBaseComponents), (baseComponents, baseComponents2) ->
            {
                baseComponents.addAll(baseComponents2);
                return baseComponents;
            });

            if (rows == 6)
            {
                rows = 0;
                totalpages++;
            }
        }

        componentBuilder.append(pages.get(page).toArray(new BaseComponent[0]));

        final TextComponent previousPageTextComponent = new TextComponent("<");
        previousPageTextComponent.setUnderlined(true);
        previousPageTextComponent.setColor(ChatColor.BLUE);
        previousPageTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rw list " + (page == 1 ? page : --page)));
        final TextComponent nextPageTextComponent = new TextComponent(">");
        nextPageTextComponent.setUnderlined(true);
        nextPageTextComponent.setColor(ChatColor.BLUE);
        nextPageTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rw list " + (page == totalpages ? page : ++page)));
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

package io.github.aquerr.regionwars.command;


import io.github.aquerr.regionwars.RegionWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class RegionWarsCommandManager implements CommandExecutor, TabCompleter
{
    private final RegionWarsCommandsList commandsList = RegionWarsCommandsList.getCommandList();
    private final RegionWarsPlugin plugin;

    public RegionWarsCommandManager(RegionWarsPlugin plugin)
    {
        this.plugin = plugin;
    }

    public void init()
    {
        commandsList.add(new VersionCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
    {
        if (args.length > 0)
        {
            return commandsList.getCommandForAlias(args[0])
                    .filter(regionWarsCommand -> regionWarsCommand.hasPermission(commandSender))
                    .map(regionWarsCommand -> regionWarsCommand.execute(commandSender, args))
                    .orElse(Boolean.FALSE); // We probably should show the usageMessage of the command and return true... instead of returning false.
        }
        else
        {
            // Show help
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args)
    {
        if (args.length > 0)
        {
            return commandsList.getCommandForAlias(args[0])
                    .filter(regionWarsCommand -> regionWarsCommand.hasPermission(commandSender))
                    .map(regionWarsCommand -> regionWarsCommand.tabComplete(commandSender, args))
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }
}

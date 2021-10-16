package io.github.aquerr.regionwars.command;


import io.github.aquerr.regionwars.RegionWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        commandsList.add(new HelpCommand());
        commandsList.add(new WandCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
    {
        try
        {
            String commandAliasToExecute = args.length > 0 ? args[0] : "help";
            Optional<RegionWarsCommand> optionalRegionWarsCommand = commandsList.getCommandForAlias(commandAliasToExecute);
            if (optionalRegionWarsCommand.isEmpty())
                return false;

            RegionWarsCommand regionWarsCommand = optionalRegionWarsCommand.get();
            if (!regionWarsCommand.hasPermission(commandSender))
                return false;

            return regionWarsCommand.execute(commandSender, args);
        }
        catch (CommandException exception)
        {
            commandSender.sendMessage(RegionWarsPlugin.ERROR_PREFIX + exception.getLocalizedMessage());
        }
        return true;
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
        else
        {
            return commandsList.getCommandsAliases().stream().toList();
        }
    }
}

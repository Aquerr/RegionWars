package io.github.aquerr.regionwars.command;


import io.github.aquerr.regionwars.RegionWarsPlugin;
import io.github.aquerr.regionwars.command.region.ListRegionsCommand;
import io.github.aquerr.regionwars.command.team.CreateTeamCommand;
import io.github.aquerr.regionwars.command.team.DeleteTeamCommand;
import io.github.aquerr.regionwars.command.team.ListTeamsCommand;
import io.github.aquerr.regionwars.command.team.TeamCommands;
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
        commandsList.add(new ListRegionsCommand(plugin.getRegionService()));
        commandsList.add(new CreateTeamCommand(plugin.getTeamService()));
        commandsList.add(new ListTeamsCommand(plugin.getTeamService()));
        commandsList.add(new DeleteTeamCommand(plugin.getTeamService()));
        commandsList.add(new TeamCommands(plugin.getTeamService()));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
    {
        boolean didSucceed = false;

        try
        {
            String commandAliasToExecute = args.length > 0 ? args[0] : "help";
            Optional<RegionWarsCommand> optionalRegionWarsCommand = commandsList.getCommandForAlias(commandAliasToExecute);
            if (optionalRegionWarsCommand.isEmpty())
                return false;

            RegionWarsCommand regionWarsCommand = optionalRegionWarsCommand.get();
            if (!regionWarsCommand.hasPermission(commandSender))
                return false;

            didSucceed = regionWarsCommand.execute(commandSender, args);
        }
        catch (CommandException exception)
        {
            commandSender.sendMessage(RegionWarsPlugin.ERROR_PREFIX + exception.getLocalizedMessage());
            return true;
        }

        if (!didSucceed)
        {
            commandSender.sendMessage(RegionWarsPlugin.ERROR_PREFIX + "Given command does not exist!");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return commandsList.getCommandsAliases().stream()
                    .filter(subCommandAlias -> subCommandAlias.startsWith(args[0]))
                    .toList();
        }
        else if (args.length > 1)
        {
            return commandsList.getCommandForAlias(args[0])
                    .map(regionWarsCommand -> regionWarsCommand.tabComplete(commandSender, args))
                    .orElse(Collections.emptyList());
        }
        else
        {
            return commandsList.getCommandsAliases().stream().toList();
        }
    }
}

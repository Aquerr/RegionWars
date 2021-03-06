package io.github.aquerr.regionwars.command;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RegionWarsCommand
{
    protected String name;
    protected String description;
    protected Set<String> aliases;
    protected String permission;
    protected String usageMessage;
    protected Map<Set<String>, RegionWarsCommand> childCommands;

    protected RegionWarsCommand(String name, Set<String> aliases, String description, String permission, String usageMessage, Map<Set<String>, RegionWarsCommand> childCommands)
    {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.permission = permission;
        this.usageMessage = usageMessage;
        this.childCommands = childCommands;
    }

    public abstract boolean execute(CommandSender commandSender, String[] arguments) throws CommandException;

    public abstract List<String> tabComplete(CommandSender commandSender, String[] arguments);

    public Set<String> getAliases()
    {
        return aliases;
    }

    public String getName()
    {
        return name;
    }

    public String getPermission()
    {
        return permission;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUsageMessage()
    {
        return usageMessage;
    }

    public Map<Set<String>, RegionWarsCommand> getChildCommands()
    {
        return childCommands;
    }

    protected void addChildCommand(Set<String> aliases, RegionWarsCommand regionWarsCommand)
    {
        this.childCommands.put(aliases, regionWarsCommand);
    }

    public boolean hasPermission(CommandSender sender)
    {
        return sender.hasPermission(this.permission);
    }
}

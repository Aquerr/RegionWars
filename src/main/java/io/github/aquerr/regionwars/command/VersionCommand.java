package io.github.aquerr.regionwars.command;

import io.github.aquerr.regionwars.PluginInfo;
import io.github.aquerr.regionwars.PluginPermissions;
import io.github.aquerr.regionwars.RegionWarsPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class VersionCommand extends RegionWarsCommand
{
    public VersionCommand()
    {
        super("version",
                Set.of("version", "v", "ver"),
                "Displays plugin version",
                PluginPermissions.VERSION_COMMAND,
                "/rw version",
                null);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments)
    {
        commandSender.sendMessage(RegionWarsPlugin.PLUGIN_PREFIX + PluginInfo.NAME + " " + PluginInfo.VERSION);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        return Collections.emptyList();
    }
}

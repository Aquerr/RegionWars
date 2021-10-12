package io.github.aquerr.regionwars.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class RegionWarsCommandsList
{
    private static final RegionWarsCommandsList INSTANCE = new RegionWarsCommandsList();

    private final Map<String, RegionWarsCommand> commands = new HashMap<>();

    private RegionWarsCommandsList()
    {

    }

    public static RegionWarsCommandsList getCommandList()
    {
        return INSTANCE;
    }

    public void add(RegionWarsCommand command)
    {
        final Set<String> aliases = command.getAliases();
        for (final String alias : aliases)
        {
            commands.put(alias, command);
        }
    }

    public Optional<RegionWarsCommand> getCommandForAlias(final String alias)
    {
        return Optional.ofNullable(commands.get(alias));
    }

    public List<RegionWarsCommand> getCommands()
    {
        return this.commands.values().stream().distinct().collect(Collectors.toList());
    }

    public Set<String> getCommandsAliases()
    {
        return commands.keySet();
    }
}

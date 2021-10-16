package io.github.aquerr.regionwars.command;

import io.github.aquerr.regionwars.PluginPermissions;
import io.github.aquerr.regionwars.RegionWarsPlugin;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class WandCommand extends RegionWarsCommand
{
    public WandCommand()
    {
        super("wand",
                Set.of("wand"),
                "Gives Region Wars Wand",
                PluginPermissions.WAND_COMMAND,
                "/rw wand",
                null);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] arguments) throws CommandException
    {
        if (!(commandSender instanceof Player))
            throw new CommandException(RegionWarsPlugin.PLUGIN_PREFIX + "Only in-game players can use this command!");

        ItemStack itemStack = new ItemStack(Material.GOLDEN_AXE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Region Wars Wand");
        itemMeta.setLore(List.of(ChatColor.DARK_PURPLE + "First position: ", ChatColor.DARK_PURPLE + "Second position: "));
        itemStack.setItemMeta(itemMeta);

        Player player = (Player) commandSender;
        player.getInventory().addItem(itemStack);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] arguments)
    {
        return Collections.emptyList();
    }
}

package org.altronmaxx.nerfphantomsfolia;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NerfphantomsCommand implements CommandExecutor {

    PluginCommand pluginCommand;
    protected NerfphantomsCommand() {
        pluginCommand = Nerfphantoms_folia.getInstance().getCommand("nerfphantoms");
        if (pluginCommand != null){
            pluginCommand.setTabCompleter(new TabCompletion());
            pluginCommand.setExecutor(this);
        }

    }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            return;
        }

        Component permissionMessage = pluginCommand.permissionMessage();
        assert (permissionMessage != null);

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("nerfphantoms.reload")) {
                sender.sendMessage(permissionMessage);
                return;
            }
            Nerfphantoms_folia.getInstance().reloadConfig();
            Nerfphantoms_folia.getInstance().config = Nerfphantoms_folia.getInstance().getConfig();

            Nerfphantoms_folia.getInstance().logger.info("Reloaded configuration");
            if (sender instanceof Player) {
                sender.sendMessage("Reloaded configuration");
            }
            return;
        }

        if (args[0].equalsIgnoreCase("kill")) {
            if (!sender.hasPermission("nerfphantoms.kill")) {
                sender.sendMessage(permissionMessage);
                return;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("Command has to be executed by a player");
                return;
            }
            Player player = (Player) sender;
            int n = PhantomUtils.killAllPhantoms(player.getWorld());
            player.sendMessage("Killed " + n + " phantoms.");
            return;
        }

        if (args[0].equalsIgnoreCase("togglespawn")) {
            if (args.length == 1) {
                if (!sender.hasPermission("nerfphantoms.disablespawn.self")) {
                    sender.sendMessage(permissionMessage);
                    return;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Command has to be executed by a player");
                    return;
                }
                Player player = (Player) sender;
                boolean state = PhantomUtils.togglePhantomSpawn(player);
                player.sendMessage((state ? "Disabled" : "Enabled")
                        + " phantom spawn for " + player.getName() + ".");
                return;
            }
            if (!sender.hasPermission("nerfphantoms.disablespawn.others")) {
                sender.sendMessage(permissionMessage);
                return;
            }
            Player victim = Bukkit.getPlayer(args[1]);
            if (victim == null) {
                sender.sendMessage("Unable to find player!");
                return;
            }
            boolean state = PhantomUtils.togglePhantomSpawn(victim);
            sender.sendMessage((state ? "Disabled" : "Enabled")
                    + " phantom spawn for " + victim.getName() + ".");
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        execute(commandSender, strings);
        return true;
    }
}

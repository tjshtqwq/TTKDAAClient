package me.tjshawa.ttkdaaclient;

import me.tjshawa.ttkdaaclient.types.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("tatako.ttkdaa.commands")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have no permission to executor this command."));
            return true;
        }
        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /ttkdaa <reload|debug|alert>"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            TTKDAAClient.configManager.reload();
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded config."));
        }
        else if (args[0].equalsIgnoreCase("debug")) {
            if (commandSender instanceof Player) {
                Player pp = (Player) commandSender;
                PlayerData data = PlayerDataManager.getPlayerData(pp.getUniqueId());
                if (data != null) {
                    if (data.debug) {
                        data.debug = false;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDebug mode disabled."));
                    } else {
                        data.debug = true;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDebug mode enabled."));
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid playerData. Please report this message to me!"));
                }
            } else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are not a player."));
            }
        }
        else if (args[0].equalsIgnoreCase("alert")) {
            if (commandSender instanceof Player) {
                Player pp = (Player) commandSender;
                PlayerData data = PlayerDataManager.getPlayerData(pp.getUniqueId());
                if (data != null) {
                    if (data.alert) {
                        data.alert = false;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAlert mode disabled."));
                    } else {
                        data.alert = true;
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAlert mode enabled."));
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid playerData. Please report this message to me!"));
                }
            } else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are not a player."));
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /ttkdaa <reload|debug|alert>"));
        }

        return true;
    }
}

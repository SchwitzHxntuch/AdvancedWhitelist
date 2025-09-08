package de.hxntuch.AWL.cmds;

import de.hxntuch.AWL.AdvancedWhitelist;
import de.hxntuch.AWL.util.WhitelistConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Whitelist_CMD implements CommandExecutor, TabExecutor {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if(s instanceof Player) {
            Player p = (Player) s;
            if(command.getName().equalsIgnoreCase("whitelist")) {
                List<String> results = new ArrayList<>();
                if (args.length == 0) {
                    return Collections.emptyList();
                }
                if(args.length == 1) {
                    List<String> firstargs = new ArrayList<>();
                    firstargs.add("list");
                    firstargs.add("add");
                    firstargs.add("remove");
                    firstargs.add("toggle");
                    firstargs.add("requests");
                    List<String> list = firstargs.stream().filter(str -> str.startsWith(args[0])).toList();
                    results.addAll(list);
                    return results;
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("add")) {
                        WhitelistConfig config = AdvancedWhitelist.getInstance().getWhitelistConfig();
                        List<String> users = new ArrayList<>(config.getRequestlist().stream().filter(user -> user.startsWith(args[1])).toList());
                        results.addAll(users);
                        users.clear();
                        return results;
                    } else if(args[0].equalsIgnoreCase("remove")) {
                        WhitelistConfig config = AdvancedWhitelist.getInstance().getWhitelistConfig();
                        List<String> users = new ArrayList<>(config.getWhitelist().stream().filter(user -> user.startsWith(args[1])).toList());
                        results.addAll(users);
                        users.clear();
                        return results;
                    }
                }
                return results;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String string, @NotNull String @NotNull [] args) {
        if(s instanceof Player) {
            Player p = (Player) s;

            WhitelistConfig config = AdvancedWhitelist.getInstance().getWhitelistConfig();

            if(p.hasPermission(config.getString("permissions.whitelist.use"))) {
                if (args.length == 0) {
                    sendHelp(p);
                } else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("list")) {
                        p.sendMessage(config.getString("message.command.list.info").replace("%amount%", String.valueOf(config.getWhitelist().size())));
                        p.sendMessage(config.getString("message.command.list.playerlist").replace("%playerlist%", config.getStringList(config.getWhitelist())));
                    } else if(args[0].equalsIgnoreCase("requests")) {
                        p.sendMessage(config.getString("message.command.requests.info").replace("%amount%", String.valueOf(config.getRequestlist().size())));
                        p.sendMessage(config.getString("message.command.requests.playerlist").replace("%playerlist%", config.getStringList(config.getRequestlist())));
                    } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(config.getBoolean("settings.whitelist.enabled")) {
                            p.sendMessage(config.getString("message.success.whitelist.off"));
                            config.toggleWhitelist(false);
                        } else {
                            p.sendMessage(config.getString("message.success.whitelist.on"));
                            config.toggleWhitelist(true);
                        }
                    } else {
                        sendHelp(p);
                    }
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("add")) {
                        String target = args[1];
                        new WhitelistConfig().addUser(p, target);
                    } else if(args[0].equalsIgnoreCase("remove")) {
                        String target = args[1];
                        new WhitelistConfig().removeUser(p, target);
                    } else {
                        sendHelp(p);
                    }
                }
            } else {
                p.sendMessage(config.getString("message.error.noperms"));
            }
        } else {
            CommandSender p = s;

            WhitelistConfig config = AdvancedWhitelist.getInstance().getWhitelistConfig();
            if(args.length == 0) {
                p.sendMessage("§8[§6Whitelist§8] §7/whitelist list");
                p.sendMessage("§8[§6Whitelist§8] §7/whitelist add <Name>");
                p.sendMessage("§8[§6Whitelist§8] §7/whitelist remove <Name>");
                p.sendMessage("§8[§6Whitelist§8] §7/whitelist toggle");
                p.sendMessage("§8[§6Whitelist§8] §7/whitelist requests");
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    p.sendMessage("§8[§6Whitelist§8] §7Whitelisted players§8: §f%amount%".replace("%amount%", String.valueOf(config.getWhitelist().size())));
                    p.sendMessage(" §8» §7%playerlist%".replace("%playerlist%", config.getStringList(new WhitelistConfig().getWhitelist())));
                } else if(args[0].equalsIgnoreCase("requests")) {
                    p.sendMessage("§8[§6Whitelist§8] §7Players asking to be whitelisted§8: §f%amount%".replace("%amount%", String.valueOf(config.getRequestlist().size())));
                    p.sendMessage(" §8» §7%playerlist%".replace("%playerlist%", config.getStringList(new WhitelistConfig().getRequestlist())));
                } else if(args[0].equalsIgnoreCase("toggle")) {
                    if(config.getBoolean("settings.whitelist.enabled")) {
                        p.sendMessage("§8[§6Whitelist§8] §eYou disabled the whitelist");
                        config.toggleWhitelist(false);
                    } else {
                        p.sendMessage("§8[§6Whitelist§8] §eYou enabled the whitelist");
                        config.toggleWhitelist(true);
                    }
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("add")) {
                    String target = args[1];
                    config.addUser(p, target);
                } else if(args[0].equalsIgnoreCase("remove")) {
                    String target = args[1];
                    config.removeUser(p, target);
                }
            }
        }
        return false;
    }

    private void sendHelp(Player p) {
        WhitelistConfig config = AdvancedWhitelist.getInstance().getWhitelistConfig();
        p.sendMessage(config.getString("message.command.alias.list"));
        if(p.hasPermission(config.getString("permissions.whitelist.use"))) {
            p.sendMessage(config.getString("message.command.alias.add"));
            p.sendMessage(config.getString("message.command.alias.remove"));
            p.sendMessage(config.getString("message.command.alias.toggle"));
            p.sendMessage(config.getString("message.command.alias.requests"));
        }
    }
}

package de.hxntuch.AWL.util;

import de.hxntuch.AWL.AdvancedWhitelist;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TransferQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhitelistConfig {

    private File file;
    private FileConfiguration configuration;
    private ArrayList<ConfigInput> sortedList;

    public WhitelistConfig() {
        file = new File("plugins/AdvancedWhitelist", "config.yml");
        configuration = YamlConfiguration.loadConfiguration(file);

        sortedList = new ArrayList<>();
    }

    public void loadConfig() {
        init();
        configuration.options().copyDefaults(true);

        for (ConfigInput configInput : sortedList) {
            if (configuration.get(configInput.getPath()) == null) {
                configuration.set(configInput.getPath(), configInput.getValue());
            }
        }
        save();
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ConfigInput> getSortedList() {
        return sortedList;
    }

    public Integer getInt(String string) {
        try {
            return configuration.getInt(string);
        } catch (Exception e) {
            configuration.set(string, getConfigInput(string).getValue());
            save();
            return (Integer) getConfigInput(string).getValue();
        }
    }

    public Boolean getBoolean(String string) {
        try {
            return configuration.getBoolean(string);
        } catch (Exception e) {
            configuration.set(string, getConfigInput(string).getValue());
            save();
            return (Boolean) getConfigInput(string).getValue();
        }
    }

    public String getString(String string) {
        try {
            return format(ChatColor.translateAlternateColorCodes('&', configuration.getString(string)
                    .replace("%prefix%", configuration.getString("message.prefix"))));
        } catch (Exception e) {
            System.out.print(string);
            System.out.print(getConfigInput(string).getValue());
            configuration.set(string, getConfigInput(string).getValue());
            save();
            return (String) getConfigInput(string).getValue();
        }
    }
    private ConfigInput getConfigInput(String string) {
        for (ConfigInput configInput : sortedList) {
            if (configInput.getPath().equalsIgnoreCase(string)) {
                return configInput;
            }
        }
        return null;
    }

    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private String format(String msg) {
        Matcher match = pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            match = pattern.matcher(msg);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', msg);
    }

    /* Config Defaults */
    public void init() {
        new ConfigInput("settings.whitelist.enabled", true);
        new ConfigInput("settings.requests.enabled", true);
        new ConfigInput("permissions.whitelist.use", "whitelist.use");
        new ConfigInput("message.prefix", "&8» #FAC732Whitelist &8┃ &7");
        new ConfigInput("message.command.alias.list", "%prefix%/whitelist list");
        new ConfigInput("message.command.alias.add", "%prefix%/whitelist add <Name>");
        new ConfigInput("message.command.alias.remove", "%prefix%/whitelist remove <Name>");
        new ConfigInput("message.command.alias.toggle", "%prefix%/whitelist toggle");
        new ConfigInput("message.command.alias.requests", "%prefix%/whitelist requests");
        new ConfigInput("message.command.list.info", "%prefix%Whitelisted players&8: &f%amount%");
        new ConfigInput("message.command.list.playerlist", " &8» &7%playerlist%");
        new ConfigInput("message.command.requests.info", "%prefix%Players waiting for confirmation&8: &f%amount%");
        new ConfigInput("message.command.requests.playerlist", " &8» &7%playerlist%");

        new ConfigInput("message.success.addedwhitelist", "%prefix%&aAdded %player% to whitelist!");
        new ConfigInput("message.success.removedwhitelist", "%prefix%&aRemoved %player% from whitelist!");
        new ConfigInput("message.success.whitelist.on", "%prefix%&aYou enabled the whitelist!");
        new ConfigInput("message.success.whitelist.off", "%prefix%&cYou disabled the whitelist!");

        new ConfigInput("message.error.alreadywhitelisted", "%prefix%&c%player% is already whitelisted!");
        new ConfigInput("message.error.notwhitelisted", "%prefix%&c%player% is not whitelisted!");
        new ConfigInput("message.error.noperms", "%prefix%&cYou are not permitted to use this command!");

        new ConfigInput("message.kick.notwhitelisted", "&cYou are not whitelisted on this server!");

        new ConfigInput("message.requests.newrequest", "%prefix%New join request from %player% &8(&2ACCEPT&8)");
        new ConfigInput("message.requests.hover", "&7Click to add %player% to the whitelist!");

        new ConfigInput("message.console.notwhitelisted", "&c%player% is not whitelisted and wanted to join!");


        File file2 = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file2);

        if(cfg.get("server.whitelist") == null) {
            cfg.set("server.whitelist", Collections.emptyList());
        }
        if(cfg.get("server.requests") == null) {
            cfg.set("server.requests", Collections.emptyList());
        }
        try {
            cfg.save(file2);
            cfg.load(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleWhitelist(boolean state) {
        configuration.set("settings.whitelist.enabled", state);

        try {
            configuration.save(file);
            configuration.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUser(Player p, String username) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> whitelist = cfg.getStringList("server.whitelist");

        if(!whitelist.contains(username)) {
            whitelist.add(username);
            p.sendMessage(getString("message.success.addedwhitelist").replace("%player%", username));

            if(cfg.getStringList("server.requests").contains(username)) {
                List<String> rquests = cfg.getStringList("server.requests");
                rquests.remove(username);

                cfg.set("server.requests" , rquests);
            }
        } else {
            p.sendMessage(getString("message.error.alreadywhitelisted").replace("%player%", username));
        }

        cfg.set("server.whitelist" , whitelist);

        try {
            cfg.save(file);
            cfg.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUser(CommandSender sender, String username) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> whitelist = cfg.getStringList("server.whitelist");

        if(!whitelist.contains(username)) {
            whitelist.add(username);
            sender.sendMessage("§8[§6Whitelist§8] §7You added §e" + username);

            if(cfg.getStringList("server.requests").contains(username)) {
                List<String> rquests = cfg.getStringList("server.requests");
                rquests.remove(username);

                cfg.set("server.requests" , rquests);
            }
        } else {
            sender.sendMessage("§8[§6Whitelist§8] §e" + username + " §7is already whitelisted!");
        }

        cfg.set("server.whitelist" , whitelist);

        try {
            cfg.save(file);
            cfg.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeUser(Player p, String username) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> whitelist = cfg.getStringList("server.whitelist");

        if(whitelist.contains(username)) {
            whitelist.remove(username);
            p.sendMessage(getString("message.success.removedwhitelist").replace("%player%", username));
        } else {
            p.sendMessage(getString("message.error.notwhitelisted").replace("%player%", username));
        }

        cfg.set("server.whitelist" , whitelist);

        try {
            cfg.save(file);
            cfg.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeUser(CommandSender sender, String username) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> whitelist = cfg.getStringList("server.whitelist");

        if(whitelist.contains(username)) {
            whitelist.remove(username);
            sender.sendMessage("§8[§6Whitelist§8] §7You removed §e" + username);
        } else {
            sender.sendMessage("§8[§6Whitelist§8] §e" + username + " §7is not whitelisted!");
        }

        cfg.set("server.whitelist" , whitelist);

        try {
            cfg.save(file);
            cfg.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getWhitelist() {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        return cfg.getStringList("server.whitelist");
    }

    public String getStringList(List<String> list) {
        if(list.isEmpty()) {
            return "§f---";
        }
        StringJoiner stringJoiner = new StringJoiner("§8,§7 ");

        for (String str : list) {
            stringJoiner.add(str);
        }

        return stringJoiner.toString();
    }

    private static ArrayList<Player> msgCooldown = new ArrayList<>();

    public void checkJoinEvent(Player p) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if(getWhitelist().contains(p.getName())) {
            removeFromList(p);
            return;
        }
        List<String> whitelist = cfg.getStringList("server.requests");
        if(!whitelist.contains(p.getName())) {
            whitelist.add(p.getName());
        }
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§6Whitelist§8] " + getString("message.console.notwhitelisted").replace("%player%", p.getName()));

        cfg.set("server.requests" , whitelist);

        if(getBoolean("settings.requests.enabled")) {
            if (!msgCooldown.contains(p)) {
                for (Player a : Bukkit.getOnlinePlayers()) {
                    if (a.hasPermission("permissions.whitelist.use")) {
                        TextComponent maptext = new TextComponent(getString("message.requests.newrequest").replace("%player%", p.getName()));
                        maptext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whitelist add " + p.getName()));
                        maptext.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getString("message.requests.hover").replace("%player%", p.getName()))));
                        a.spigot().sendMessage(maptext);
                    }
                }

                msgCooldown.add(p);
                Bukkit.getScheduler().runTaskLater(AdvancedWhitelist.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        msgCooldown.remove(p);
                    }
                }, 20 * 60);
            }
        }

        try {
            cfg.save(file);
            cfg.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFromList(Player p) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if(getRequestlist().contains(p.getName())) {

            List<String> whitelist = cfg.getStringList("server.requests");
            whitelist.remove(p.getName());

            cfg.set("server.requests" , whitelist);

            try {
                cfg.save(file);
                cfg.load(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFromList(String username) {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if(getRequestlist().contains(username)) {
            List<String> whitelist = cfg.getStringList("server.requests");
            whitelist.remove(username);

            cfg.set("server.requests" , whitelist);

            try {
                cfg.save(file);
                cfg.load(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getRequestlist() {
        File file = new File("plugins/AdvancedWhitelist/whitelist.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        return cfg.getStringList("server.requests");
    }
}

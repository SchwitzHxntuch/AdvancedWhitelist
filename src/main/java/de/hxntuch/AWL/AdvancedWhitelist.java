package de.hxntuch.AWL;

import de.hxntuch.AWL.cmds.Whitelist_CMD;
import de.hxntuch.AWL.listener.JoinListener;
import de.hxntuch.AWL.util.WhitelistConfig;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedWhitelist extends JavaPlugin {

    private static AdvancedWhitelist instance;

    public static AdvancedWhitelist getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        whitelistConfig = new WhitelistConfig();

        getServer().getConsoleSender().sendMessage("§8[§6Whitelist§8] §eLoading...");
        whitelistConfig.loadConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        getCommand("whitelist").setExecutor(new Whitelist_CMD());

        getServer().getConsoleSender().sendMessage("§8[§6Whitelist§8] §aLoaded");
    }

    private WhitelistConfig whitelistConfig;

    public WhitelistConfig getWhitelistConfig() {
        return whitelistConfig;
    }
}

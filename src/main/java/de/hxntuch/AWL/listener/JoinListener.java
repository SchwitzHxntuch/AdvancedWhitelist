package de.hxntuch.AWL.listener;

import de.hxntuch.AWL.AdvancedWhitelist;
import de.hxntuch.AWL.util.WhitelistConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onjoin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        WhitelistConfig config = AdvancedWhitelist.getInstance().getWhitelistConfig();
        if(config.getBoolean("settings.whitelist.enabled")) {
            if (!config.getWhitelist().contains(p.getName())) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, config.getString("message.kick.notwhitelisted"));

                config.checkJoinEvent(p);
            } else {
                config.removeFromList(p);
            }
        }
    }
}

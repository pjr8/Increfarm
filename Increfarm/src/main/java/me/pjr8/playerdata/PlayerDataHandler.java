package me.pjr8.playerdata;

import me.pjr8.Main;
import me.pjr8.update.UpdateEvent;
import me.pjr8.update.UpdateType;
import me.pjr8.utility.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerDataHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.playerDataAccess.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Main.playerDataAccess.savePlayerData(event.getPlayer(), true);
    }

    @EventHandler
    public void saveDataOften(UpdateEvent event) {
        if (event.getType() != UpdateType.MIN_16) {
            return;
        }
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Main.playerDataAccess.savePlayerData(player, false);
            i++;
        }
        System.out.println("[Increfarm] Player Dated Saved [" + i + "]");
    }

}

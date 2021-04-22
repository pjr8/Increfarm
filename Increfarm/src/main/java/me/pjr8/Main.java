package me.pjr8;

import me.pjr8.gameplay.cropmechanics.CropMechanics;
import me.pjr8.gameplay.cropmechanics.commands.CommandFarm;
import me.pjr8.playerdata.PlayerDataHandler;
import me.pjr8.playerdata.PlayerDataHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.pjr8.commands.CommandTest;
import me.pjr8.update.UpdateService;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static ProtocolManager protocolManager;
    public static PlayerDataHolder playerDataAccess;
    public static CropMechanics cropMechanics;

    public Main() {

    }
    public void onEnable() {
        plugin = this;
        playerDataAccess = new PlayerDataHolder();
        cropMechanics = new CropMechanics();
        protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(new PlayerDataHandler(), this);
        Bukkit.getPluginManager().registerEvents(cropMechanics, this);
        this.getCommand("test").setExecutor(new CommandTest());
        this.getCommand("farm").setExecutor(new CommandFarm());
        UpdateService updateService = new UpdateService(this);
        updateService.start();
        System.out.println("Increfarm has been enabled");
    }

    public void onDisable() {
        UpdateService updateService = new UpdateService(this);
        updateService.stop();
        playerDataAccess.savePlayerDataOnShutdown();
        System.out.println("Increfarm has been disabled");
    }
}



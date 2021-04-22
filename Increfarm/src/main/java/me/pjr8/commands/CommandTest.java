package me.pjr8.commands;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.pjr8.Main;
import me.pjr8.playerdata.PlayerData;
import me.pjr8.utility.Messages;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CommandTest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command line, String label, String[] args) {
        Player player = (Player) sender;
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        if (args.length < 1) {
            Messages.sendMessage(player, "UUID: " + playerData.getUuid(), "IGN: " + playerData.getName(), "Money: " + playerData.getMoney(), "Crop Level: " + playerData.getCropLevel());
            Messages.sendMessage(player, "Crop Upgrades:", "- " + playerData.getUpgrades()[0], "- " + playerData.getUpgrades()[1], "- " + playerData.getUpgrades()[2]);
            return false;
        }
        return false;
    }

}

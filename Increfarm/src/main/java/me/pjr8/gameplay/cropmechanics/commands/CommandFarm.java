package me.pjr8.gameplay.cropmechanics.commands;

import me.pjr8.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFarm implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        Main.cropMechanics.playerFarmGUI(player, "menu");
        return false;
    }

}

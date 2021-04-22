package me.pjr8.playerdata;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.pjr8.Main;
import me.pjr8.utility.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataHolder {
    public HashMap<Player, PlayerData> playerDataOnline = new HashMap<Player, PlayerData>();

    public void savePlayerData(Player player, boolean takeOffList) {
        PlayerData playerData = playerDataOnline.get(player);
        playerData.setLastLogin(System.currentTimeMillis());
        if (takeOffList) {
            try {
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
                objectMapper.writeValue(new File(Main.plugin.getDataFolder() + "\\" + player.getUniqueId().toString() + ".yaml"), playerData);
                playerDataOnline.remove(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
                objectMapper.writeValue(new File(Main.plugin.getDataFolder() + "\\" + player.getUniqueId().toString() + ".yaml"), playerData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            File playerFile = new File(Main.plugin.getDataFolder() + "\\" + uuid.toString() + ".yaml");
            if (playerFile.exists()) {
                ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
                PlayerData playerData = objectMapper.readValue(playerFile, PlayerData.class);
                playerDataOnline.put(player, playerData);
            } else {
                PlayerData playerData = new PlayerData(uuid);
                playerData.setUUID(uuid);
                playerData.setName(player.getName());
                playerData.setFirstLogin(System.currentTimeMillis());
                playerData.setLastLogin(System.currentTimeMillis());
                playerData.setMoney(10);
                playerData.setCropLevel(0);
                playerData.setUpgrades(new int[]{0, 0, 0});

                //EXPERIMENTAL
                ArrayList<Material> data = new ArrayList<Material>();
                data.add(Material.DIRT);
                data.add(Material.GOLD_BLOCK);
                data.add(Material.DIAMOND_ORE);
                data.add(Material.EMERALD_BLOCK);
                playerData.setTestBlockData(data);

                ArrayList<BlockPosition> blockData = new ArrayList<BlockPosition>();
                blockData.add(new BlockPosition(0, 0, 0));
                blockData.add(new BlockPosition(1, 0, 0));
                blockData.add(new BlockPosition(-1, 0, 0));
                blockData.add(new BlockPosition(0, 0, 1));
                blockData.add(new BlockPosition(0, 0, -1));
                playerData.setTestBlockPosition(blockData);

                //EXPERIMENTAL

                playerDataOnline.put(player, playerData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void savePlayerDataOnShutdown() {
        int i = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            savePlayerData(player, true);
            player.kickPlayer(Colors.GREEN + Colors.BOLD + "Server Restarting");
            i++;
        }
        System.out.println("All player data saved [" + i + "]");
    }
}

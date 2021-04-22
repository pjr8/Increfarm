package me.pjr8.gameplay.cropmechanics;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import me.pjr8.Main;
import me.pjr8.playerdata.PlayerData;
import me.pjr8.update.UpdateEvent;
import me.pjr8.update.UpdateType;
import me.pjr8.utility.Colors;
import me.pjr8.utility.Messages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CropMechanics implements Listener {


    //All players online crop data
    public HashMap<Player, Crop> playerCropDataHolder = new HashMap<Player, Crop>();

    //Timer to delete hologram for money gained
    public HashMap<Hologram, Long> deleteHologram = new HashMap<Hologram, Long>();


    @EventHandler
    public void checkPlayersCropProgress(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            Crop playerCropData = playerCropDataHolder.get(player);
            PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
            if (playerCropData != null && playerCropData.canCropUpdate() && playerData.getCropLevel() == 1) {
                int stage = playerCropData.getStage();
                WrappedBlockData wrappedBlockData;
                PacketContainer fakeBlock = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                fakeBlock.getBlockPositionModifier().write(0, new BlockPosition(0, 150, 0));
                if (stage < 7) {
                    wrappedBlockData = WrappedBlockData.createData(Material.WHEAT, playerCropData.getStage());
                    fakeBlock.getBlockData().write(0, wrappedBlockData);
                    playerCropData.setStage(stage + 1);
                    try {
                        Main.protocolManager.sendServerPacket(player, fakeBlock);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(
                                "Cannot send packet " + fakeBlock, e);
                    }
                } else {
                    wrappedBlockData = WrappedBlockData.createData(Material.WHEAT, 0);
                    fakeBlock.getBlockData().write(0, wrappedBlockData);
                    playerCropData.setStage(0);

                    playerCropData.setLastCropCompletion(System.currentTimeMillis());

                    playerCropCompletionMoney(player);

                    try {
                        Main.protocolManager.sendServerPacket(player, fakeBlock);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(
                                "Cannot send packet " + fakeBlock, e);
                    }
                }

            }

        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        startPlayerCropData(event.getPlayer());
    }

    @EventHandler
    public void onDeleteHologram(UpdateEvent event) {
        if (!event.getType().equals(UpdateType.FASTEST)) {
            return;
        }
        for (Map.Entry<Hologram, Long> entry : deleteHologram.entrySet()) {
            Hologram hologram = entry.getKey();
            Long time = entry.getValue();

            if (System.currentTimeMillis() > time + 500) {
                hologram.delete();
            }
        }

    }

    @EventHandler
    public void playerInteractFarmGUI(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        ItemStack itemClicked = event.getCurrentItem();
        String title = ChatColor.stripColor(event.getView().getTitle());
        int[] upgrades = playerData.getUpgrades();
        if (itemClicked == null) {
            return;
        }
        if (title.contains("farm") && itemClicked.getType() == Material.WHEAT) {
            if (playerData.getCropLevel() == 1) {
                playerFarmGUI(player, "wheat");
            } else {
                if (playerData.playerBuy(10)) {
                    event.setCancelled(true);
                    player.closeInventory();
                    Messages.sendMessage(player, Colors.GREEN + "Successfully bought wheat crop");
                    playerData.setCropLevel(1);
                    startPlayerCropData(player);
                }
            }
        } else if (title.contains("Wheat")) {
            event.setCancelled(true);
            if (itemClicked.getType() == Material.WHEAT && upgrades[0] < 10) {
                if (playerData.playerBuy(costCropUpgrade(player, 1))) {
                    upgrades[0] += 1;
                    playerData.setUpgrades(upgrades);
                    player.sendMessage(Colors.GREEN + "Level " + upgrades[0] + " value upgrade achived");
                    playerFarmGUI(player, "wheat");
                }
            } else if (itemClicked.getType() == Material.SUGAR && upgrades[1] < 10) {
                if (playerData.playerBuy(costCropUpgrade(player, 2))) {
                    upgrades[1] += 1;
                    playerData.setUpgrades(upgrades);
                    player.sendMessage(Colors.GREEN + "Level " + upgrades[1] + " speed upgrade achived");
                    playerFarmGUI(player, "wheat");
                    calculatePlayerCropGrowthTimeTotal(player);
                }
            } else if (itemClicked.getType() == Material.RABBIT_FOOT && upgrades[2] < 10){
                if (playerData.playerBuy(costCropUpgrade(player, 3))) {
                    upgrades[2] += 1;
                    playerData.setUpgrades(upgrades);
                    player.sendMessage(Colors.GREEN + "Level " + upgrades[2] + " tenfold upgrade achived");
                    playerFarmGUI(player, "wheat");
                }
            }
        }
    }

    public void playerFarmGUI(Player player, String menu) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        if (menu.contains("menu")) {
            Inventory farmGUI = Bukkit.createInventory(null, 27, Colors.BLACK + player.getName() + "'s farm");
            farmGUI.setItem(10, guiCropItemData(player));
            player.openInventory(farmGUI);
        } else if (menu.equals("wheat")) {
            Inventory wheatGUI = Bukkit.createInventory(null, 27, Colors.YELLOW + "Wheat Crop");
            wheatGUI.setItem(10, upgradeGUIItem(player, "wheat", 1));
            wheatGUI.setItem(13, upgradeGUIItem(player, "wheat", 2));
            wheatGUI.setItem(16, upgradeGUIItem(player, "wheat", 3));
            player.openInventory(wheatGUI);
        }
    }

    private ItemStack upgradeGUIItem(Player player, String crop, int upgradeType) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        if (crop.equals("wheat")) {
            if (upgradeType == 1) {
                return createItem(Material.WHEAT, Colors.GREEN + "Value Upgrade", "",
                        Colors.DARK_GREEN + "Current Level: " + playerData.getUpgrades()[0],
                        Colors.YELLOW + "Next Level Cost: $" + costCropUpgrade(player, 1));
            } else if (upgradeType == 2) {
                return createItem(Material.SUGAR, Colors.GREEN + "Speed Upgrade", "",
                        Colors.DARK_GREEN + "Current Level: " + playerData.getUpgrades()[1],
                        Colors.YELLOW + "Next Level Cost: $" + costCropUpgrade(player, 2));
            } else if (upgradeType == 3) {
                return createItem(Material.RABBIT_FOOT, Colors.GREEN + "Tenfold Upgrade", "",
                        Colors.DARK_GREEN + "Current Level: " + playerData.getUpgrades()[2],
                        Colors.YELLOW + "Next Level Cost: $" + costCropUpgrade(player, 3));
            }
        }
        return null;
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack guiCropItemData(Player player) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        int[] upgrades = playerData.getUpgrades();
        if (playerData.getCropLevel() == 1) {
            return createItem(Material.WHEAT, Colors.GREEN + "Wheat", "", Colors.YELLOW + "Crop Value Upgrade: " + upgrades[0], Colors.YELLOW + "Speed Upgrade: " + upgrades[1], Colors.YELLOW + "Tenfold Upgrade: " + upgrades[2]);
        } else {
            return createItem(Material.WHEAT, Colors.RED + Colors.BOLD + "LOCKED: Wheat", "", Colors.GRAY + "Cost: " + Colors.GREEN + "10", Colors.WHITE + "Click to buy your first crop!");
        }
    }

    public void playerCropCompletionMoney(Player player) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        int moneyToAdd = calculatePlayerCropMoney(player);
        playerData.setMoney(playerData.getMoney() + moneyToAdd);
        onTestHolographicDisplays(player, moneyToAdd);
    }

    public int calculatePlayerCropMoney(Player player) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        int moneyToGive = 0;
        int valueUpgrade = playerData.getUpgrades()[0];
        int tenfoldUpgrade = playerData.getUpgrades()[2];
        Random random = new Random();
        if (playerData.getCropLevel() == 1) {
            moneyToGive = 10;
            if (valueUpgrade > 0) {
                moneyToGive = 10 + valueUpgrade;
            }
            if (tenfoldUpgrade > 0) {
                int chance = random.nextInt(1000) + 1;
                if (chance < tenfoldUpgrade * 15) { //15, or rather 1.5% per upgrade
                    moneyToGive = moneyToGive * 10;
                }
            }
        }

        return moneyToGive;
    }

    public long calculatePlayerCropGrowthTimeTotal(Player player) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        int speedUpgrade = playerData.getUpgrades()[1];
        if (speedUpgrade > 0) {
            return Math.round(5000 - (5000 * (speedUpgrade * 0.025)));
        }
        return 5000;
    }

    public int costCropUpgrade(Player player, int upgrade) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        int valueUpgrade = playerData.getUpgrades()[0];
        int speedUpgrade = playerData.getUpgrades()[1];
        int tenfoldUpgrade = playerData.getUpgrades()[2];

        if (upgrade == 1) {
            int[] cost = new int[]{25, 50, 75, 100, 100, 125, 150, 200, 300, 400, 0};
            return cost[valueUpgrade];
        } else if (upgrade == 2) { //Speed Upgrade
            int[] cost = new int[]{25, 50, 75, 100, 100, 125, 150, 200, 300, 400, 0};
            return cost[speedUpgrade];
        } else if (upgrade == 3) { //Tenfold Upgrade
            int[] cost = new int[]{25, 50, 75, 100, 100, 125, 150, 200, 300, 400, 0};
            return cost[tenfoldUpgrade];
        }
        return 0;
    }

    public void startPlayerCropData(Player player) {
        PlayerData playerData = Main.playerDataAccess.playerDataOnline.get(player);
        if (playerData.getCropLevel() == 1 && playerData != null) {
            Crop crop = new Crop(player);
            crop.setStage(0);
            crop.setCropTimeGrowthTotal(calculatePlayerCropGrowthTimeTotal(player));
            playerCropDataHolder.put(player, crop);
        }

    }

    public void onTestHolographicDisplays(Player player, Integer value) {
        Hologram hologram = HologramsAPI.createHologram(Main.plugin, new Location(Bukkit.getServer().getWorld("world"), 0.5, 152, 0.5));
        VisibilityManager visibilityManager = hologram.getVisibilityManager();
        visibilityManager.setVisibleByDefault(false);
        visibilityManager.showTo(player);
        hologram.appendTextLine(Colors.GREEN + "+$" + value);
        deleteHologram.put(hologram, System.currentTimeMillis());
    }

    @EventHandler
    public void onTestBlockMultiChange(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) {
            return;
        }
        ArrayList<Location> blockLocations = new ArrayList<Location>();
        blockLocations.add(new Location(Bukkit.getWorld("world"), 8, 150, 8));
        blockLocations.add(new Location(Bukkit.getWorld("world"), 7, 150, 8));
        blockLocations.add(new Location(Bukkit.getWorld("world"), 9, 150, 8));
        blockLocations.add(new Location(Bukkit.getWorld("world"), 8, 150, 7));
        blockLocations.add(new Location(Bukkit.getWorld("world"), 8, 150, 9));

        ArrayList<WrappedBlockData> blockTypes = new ArrayList<WrappedBlockData>();
        blockTypes.add(WrappedBlockData.createData(Material.DIAMOND_BLOCK));
        blockTypes.add(WrappedBlockData.createData(Material.IRON_BLOCK));
        blockTypes.add(WrappedBlockData.createData(Material.COAL_BLOCK));
        blockTypes.add(WrappedBlockData.createData(Material.EMERALD_BLOCK));
        blockTypes.add(WrappedBlockData.createData(Material.GOLD_BLOCK)); //Goes from 0-4

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Main.protocolManager.sendServerPacket(player, createMultiBlockPacketContainer(blockLocations, blockTypes));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public PacketContainer createMultiBlockPacketContainer(ArrayList<Location> blockLocations, ArrayList<WrappedBlockData> blockTypes) {
        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        short[] shortArray = new short[blockLocations.size()];
        for (int i = 0 ; i < blockLocations.size(); i++) {
            packet.getSectionPositions().writeSafely(i, new BlockPosition(blockLocations.get(i).getBlockX() >> 4, blockLocations.get(i).getBlockY() >> 4, blockLocations.get(i).getBlockZ() >> 4));
            shortArray[i] = (short) ((blockLocations.get(i).getBlockX() & 0xF) << 8 | (blockLocations.get(i).getBlockZ() & 0xF) << 4 | (blockLocations.get(i).getBlockY() & 0xF));
        }
        WrappedBlockData[] wrappedBlockData = new WrappedBlockData[blockTypes.size()];
        for (int i = 0 ; i < blockTypes.size(); i++) {
            wrappedBlockData[i] = blockTypes.get(i);
        }
        packet.getShortArrays().writeSafely(0, shortArray);
        packet.getBlockDataArrays().writeSafely(0, wrappedBlockData);
        return packet;
    }

    /*@EventHandler
    public void onTestBlockCrop(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOWEST) {
            return;
        }
        PacketContainer packet = Main.protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        packet.getSectionPositions().writeSafely(0, new BlockPosition(8 >> 4, 150 >> 4, 8 >> 4));
        packet.getSectionPositions().writeSafely(1, new BlockPosition(8 >> 4, 151 >> 4, 8 >> 4));

        packet.getBlockDataArrays().writeSafely(0, new WrappedBlockData[] { WrappedBlockData.createData(Material.COAL_BLOCK),
                WrappedBlockData.createData(Material.IRON_BLOCK) });

        packet.getShortArrays().writeSafely(0, new short[]{((8 & 0xF) << 8 | (8 & 0xF) << 4 | (150 & 0xF)), ((8 & 0xF) << 8 | (8 & 0xF) << 4 | (151 & 0xF)) });


        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Main.protocolManager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public WrappedBlockData[] createWrappedBlockDataArray(Material[] blockTypes) {
        WrappedBlockData[] arrayToReturn = new WrappedBlockData[]{};
        for (int i = 0 ; i > blockTypes.length ; i++) {
            arrayToReturn[i] = WrappedBlockData.createData(blockTypes[i]);
        }
        return arrayToReturn;
    } */
}
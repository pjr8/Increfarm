package me.pjr8.playerdata;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import me.pjr8.utility.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private String name;
    private long firstLogin;
    private long lastLogin;
    private int money;
    private int cropLevel;
    private int[] upgrades;
    private ArrayList<Material> testBlockData;
    private ArrayList<BlockPosition> testBlockPosition;


    public PlayerData() {
        super();
    }

    public PlayerData(UUID playerUUID) {
        this.uuid = playerUUID;
    }

    public PlayerData(UUID uuid, String name, long firstLogin, long lastLogin, int money, int cropLevel, int[] upgrades, ArrayList<Material> testBlockData, ArrayList<BlockPosition> testBlockPosition) {
        this.uuid = uuid;
        this.name = name;
        this.money = money;
        this.cropLevel = cropLevel;
        this.upgrades = upgrades;
        this.testBlockData = testBlockData;
        this.testBlockPosition = testBlockPosition;
    }

    public UUID setUUID(UUID playerUUID) {
        return this.uuid = playerUUID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getCropLevel() {
        return cropLevel;
    }

    public void setCropLevel(int cropLevel) {
        this.cropLevel = cropLevel;
    }

    public int[] getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(int[] upgrades) {
        this.upgrades = upgrades;
    }

    //EXPERIMENTAL


    public ArrayList<Material> getTestBlockData() {
        return testBlockData;
    }

    public void setTestBlockData(ArrayList<Material> testBlockData) {
        this.testBlockData = testBlockData;
    }

    public ArrayList<BlockPosition> getTestBlockPosition() {
        return testBlockPosition;
    }

    public void setTestBlockPosition(ArrayList<BlockPosition> testBlockPosition) {
        this.testBlockPosition = testBlockPosition;
    }

    //EXPERIMENTAL

    public boolean playerBuy(int cost) {
        if (this.money - cost >= 0) {
            this.money -= cost;
            return true;
        } else {
            Bukkit.getPlayer(this.uuid).sendMessage(Colors.RED + "You cannot afford this.");
            return false;
        }
    }
}

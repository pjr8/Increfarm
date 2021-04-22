package me.pjr8.gameplay.cropmechanics;

import org.bukkit.entity.Player;

public class Crop {

    private Player player;

    private int stage;
    private long cropTimeGrowthTotal;
    private long lastCropCompletion;

    public Crop() {
        super();
    }

    public Crop(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getCropTimeGrowthTotal() {
        return cropTimeGrowthTotal;
    }

    public void setCropTimeGrowthTotal(long cropTimeGrowthTotal) {
        this.cropTimeGrowthTotal = cropTimeGrowthTotal;
    }

    public long getLastCropCompletion() {
        return lastCropCompletion;
    }

    public void setLastCropCompletion(Long lastCropCompletion) {
        this.lastCropCompletion = lastCropCompletion;
    }

    public boolean canCropUpdate() {
        if ((this.cropTimeGrowthTotal / 8) * (stage + 1) < System.currentTimeMillis() - this.lastCropCompletion) {
            return true;
        }
        return false;
    }
}

package me.pjr8.utility;

import java.util.Arrays;

import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;



/**
 * Message utilities.
 *
 * @author Thortex
 */
public final class Messages {
    private Messages() {}

    /**
     * Play a sound to all players in the server.
     *
     * @param sound the sound
     * @param vol   the volume
     * @param pitch the pitch
     */

    public static void broadcastSound(Sound sound, float vol, float pitch) {
        Bukkit.getOnlinePlayers().forEach(player ->
                player.playSound(player.getLocation(), sound, vol, pitch));
    }

    /**
     * Send a message.
     *
     * @param player the player
     * @param msgs   the message
     */
    public static void sendMessage(Player player, String... msgs) {
        Arrays.stream(msgs).forEach(msg -> player.sendMessage(msg));
    }

    /**
     * Send a centralized message.
     *
     * @param player the player
     * @param msgs   the message
     */
    public static void sendCentralizedMessage(Player player, String... msgs) {
        Arrays.stream(msgs).forEach(msg -> player.sendMessage(StringUtils.center(msg, 70)));
    }

    /**
     * Send a centralized message to all players.
     *
     * @param msgs the message
     */
    public static void sendGlobalCentralizedMessage(String... msgs) {
        Bukkit.getOnlinePlayers().forEach(o -> sendCentralizedMessage(o, msgs));
    }

    /**
     * Send an action bar message.
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    /**
     * Send a title.
     *
     * @param player the player
     * @param title  the title
     */
    public static void sendTitle(Player player, String title) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
                ChatColor.translateAlternateColorCodes('&', title.replaceAll("%player%", player.getName())) +
                "\"}");

        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, cbc);
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Send a title.
     *
     * @param player  the player
     * @param title   the title
     * @param fadeIn  fade in delay
     * @param fadeOut fade out delay
     * @param stay    stay delay
     */
    public static void sendTitle(Player player, String title, int fadeIn, int fadeOut, int stay) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
                ChatColor.translateAlternateColorCodes('&', title.replaceAll("%player%", player.getName())) +
                "\"}");

        PacketPlayOutTitle packet =
                new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, cbc, fadeIn, stay, fadeOut);
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Send a subtitle.
     *
     * @param player   the player
     * @param subTitle the subtitle
     */
    public static void sendSubTitle(Player player, String subTitle) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
                ChatColor.translateAlternateColorCodes('&', subTitle.replaceAll("%player%", player.getName())) +
                "\"}");

        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, cbc);
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }


    public static void broadcastTitle(String title, int fadeIn, int fadeOut, int stay) {
        Bukkit.getOnlinePlayers().forEach(player ->
                sendTitle(player, title, fadeIn, fadeOut, stay));
    }


    public static void broadcastSubtitle(String subtitle) {
        Bukkit.getOnlinePlayers().forEach(player -> sendSubTitle(player, subtitle));
    }

}

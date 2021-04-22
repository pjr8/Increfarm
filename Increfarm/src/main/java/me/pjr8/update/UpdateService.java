package me.pjr8.update;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * When started, it initializes the update task and event.
 *
 * @author Thortex
 */
public class UpdateService {
    private final JavaPlugin plugin;
    private int taskId;

    public UpdateService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new UpdateTask(), 1, 1);
    }

    public void stop() {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }

    private class UpdateTask implements Runnable {
        private UpdateTask() {
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
        }

        public void run() {
            Arrays.stream(UpdateType.values())
                    .filter(UpdateType::elapsed)
                    .forEach(o -> plugin.getServer().getPluginManager().callEvent(new UpdateEvent(o)));
        }
    }
}

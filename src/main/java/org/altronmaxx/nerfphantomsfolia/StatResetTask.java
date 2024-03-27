package org.altronmaxx.nerfphantomsfolia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class StatResetTask implements ScheduledTask, Runnable {

    private Nerfphantoms_folia plugin;

    StatResetTask(Nerfphantoms_folia plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public boolean isRepeatingTask() {
        return true;
    }

    @Override
    public @NotNull CancelledState cancel() {
        return null;
    }

    @Override
    public @NotNull ExecutionState getExecutionState() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return ScheduledTask.super.isCancelled();
    }

    @Override
    public void run() {
        plugin.phantomDisabled.forEach((player) -> {
            if (plugin.isWorldEnabled(player.getWorld())) {
                player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            }
        });
    }
}
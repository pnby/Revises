package me.pink.revises.api;

import me.pink.revises.Revises;
import org.
        bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private final JavaPlugin pluginInstance = Revises.instance;

    public void syncWithMain(Runnable runnable) {
        Bukkit.getScheduler().runTask(pluginInstance, runnable);
    }

    public void runTask(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(pluginInstance, runnable, delay);
    }

    public ScheduledFuture<?> runTaskAsync(Runnable runnable, long delay) {
        return EXECUTOR_SERVICE.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> runRepeatingTask(Runnable runnable, long delay, long period) {
        return EXECUTOR_SERVICE.scheduleAtFixedRate(()-> syncWithMain(runnable), delay, period, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> runRepeatingTaskAsync(Runnable runnable, long delay, long period) {
        return EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        EXECUTOR_SERVICE.shutdown();
    }
}
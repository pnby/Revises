package me.pink.revises


import com.j256.ormlite.logger.LocalLog
import me.pink.revises.api.internal.Metrics
import me.pink.revises.api.internal.Metrics.SimplePie
import me.pink.revises.commands.ReviseCommand
import me.pink.revises.database.repositories.CheckRepository
import me.pink.revises.listeners.*
import me.pink.revises.listeners.entity.EntityDamageListener
import me.pink.revises.listeners.entity.EntityInventoryListener
import me.pink.revises.listeners.player.PlayerInteractListener
import me.pink.revises.listeners.player.PlayerLeaveListener
import me.pink.revises.listeners.player.PlayerMoveListener
import me.pink.revises.managers.AFKTrackManager
import me.pink.revises.managers.TaskSchedulerManager
import me.pink.revises.tabcompeters.ReviseTabCompleter
import me.pink.revises.utils.config.ConfigurationType
import me.pink.revises.utils.config.Configurations
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class Revises : JavaPlugin() {
    lateinit var configurations: Configurations
    lateinit var reasonsFile: File

    override fun onLoad() {
        instance = this
        loadConfig()
    }


    override fun onEnable() {
        setupLogging()
        setupMetrics()

        TaskSchedulerManager.runReduceTimeTask()
        TaskSchedulerManager.runCheckMessageTask()

        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(PlayerLeaveListener(this), this)
        server.pluginManager.registerEvents(PlayerInteractListener(), this)
        server.pluginManager.registerEvents(EntityInventoryListener(), this)
        server.pluginManager.registerEvents(PlayerMoveListener(), this)
        server.pluginManager.registerEvents(EntityDamageListener(), this)

        if (ReviseConfig.AFKSystemIsToggle) {
            server.pluginManager.registerEvents(AFKTrackManager, this)
            TaskSchedulerManager.runAFKTrackTask()
        }

        if (ReviseConfig.systemStartCooldown > -1 || ReviseConfig.systemLogsCooldown > -1) {
            TaskSchedulerManager.runCooldownTimeReduceTask()
        }

        getCommand("revise")?.setExecutor(ReviseCommand())
        getCommand("revise")?.tabCompleter = ReviseTabCompleter()

        try {
            CheckRepository.createTables()
        } catch (e: Exception) {
            if (CheckRepository.mockRequest()) return
            this.logger.warning("Failed to create tables (created earlier?)")
        }
    }

    override fun onDisable() {
        CheckRepository.close()
    }

    fun loadConfig() {
        configurations = Configurations(this)
        configurations.load()
        configurations.load()
        reasonsFile = configurations.load()!!
    }

    companion object {
        lateinit var instance: Revises
    }

    private fun setupMetrics() {
        val metrics = Metrics(this, 22162)

        metrics.addCustomChart(SimplePie("server_version") { server.version })
        metrics.addCustomChart(SimplePie("java_version", this::getJavaVersion))
    }

    private fun getJavaVersion(): String {
        return System.getProperty("java.version")
    }

    private fun setupLogging() {
        System.setProperty("com.j256.ormlite.logger.type", "LOCAL")
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR")
    }


    fun getStringFromConfig(key: String): String {
        return configurations.get(ConfigurationType.CONFIG).getString(key) ?: ""
    }

    fun getStringFromMessages(key: String): String {
        return configurations.get(ConfigurationType.MESSAGES).getString(key) ?: ""
    }

    fun getListFromMessages(key: String): MutableList<*> {
        return configurations.get(ConfigurationType.MESSAGES).getList(key) ?: mutableListOf<Any>()
    }

    fun getDoubleFromConfig(key: String): Double {
        return configurations.get(ConfigurationType.CONFIG).getDouble(key)
    }

    fun getIntFromConfig(key: String): Int {
        return configurations.get(ConfigurationType.CONFIG).getInt(key)
    }

    fun getBoolFromConfig(key: String): Boolean {
        return configurations.get(ConfigurationType.CONFIG).getBoolean(key)
    }

    fun getListFromConfig(key: String): List<Any?> {
        return configurations.get(ConfigurationType.CONFIG).getList(key) ?: emptyList()
    }
}

fun String.convertColor(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}
package me.pink.revises.managers

import me.pink.revises.api.Scheduler
import me.pink.revises.convertColor
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit


class BossBarManager(
    private val color: BarColor,
    private var initialTimeInSeconds: Int,
    private val scheduler: Scheduler,
) {
    private val bossBars: MutableMap<Player, BossBar> = HashMap()
    private val inspectorBossBars: MutableMap<Player, BossBar> = HashMap()
    private val suspectStopBossBars: MutableMap<Player, BossBar> = HashMap()
    private val playerTimes: MutableMap<Player, Int> = HashMap()

    private fun formatTime(seconds: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun updateSuspectBossBar(player: Player, timeInSeconds: Int) {
        val formattedTime = formatTime(timeInSeconds)
        val progress = (timeInSeconds.toDouble() / initialTimeInSeconds).coerceIn(0.0, 1.0)
        bossBars[player]?.let {
            it.progress = progress
            it.setTitle(ReviseConfig.bossBarSuspectText.replace("%time%", formattedTime).convertColor())
        }
    }

    private fun updateInspectorBossBar(player: Player, timeInSeconds: Int) {
        val formattedTime = formatTime(timeInSeconds)
        val progress = (timeInSeconds.toDouble() / initialTimeInSeconds).coerceIn(0.0, 1.0)
        inspectorBossBars[player]?.let {
            it.progress = progress
            it.setTitle(ReviseConfig.bossBarInspectorText.replace("%time%", formattedTime).convertColor())
        }
    }

    private fun startTimer(player: Player) {
        scheduler.runRepeatingTaskAsync({
            val timeInSeconds = playerTimes[player] ?: return@runRepeatingTaskAsync
            if (timeInSeconds > 0) {
                playerTimes[player] = timeInSeconds - 1
                updateSuspectBossBar(player, timeInSeconds)
                updateInspectorBossBar(player, timeInSeconds)
            } else {
                bossBars[player]?.removeAll()
                inspectorBossBars[player]?.removeAll()
                return@runRepeatingTaskAsync
            }
        }, 0L, 1000L)
    }

    fun addPlayer(player: Player) {
        if (inspectorBossBars.containsKey(player)) return

        val bossBar = Bukkit.createBossBar("", color, BarStyle.SOLID)
        bossBar.isVisible = true
        bossBar.addPlayer(player)
        bossBars[player] = bossBar

        playerTimes[player] = initialTimeInSeconds
        startTimer(player)
    }

    fun addInspector(player: Player) {
        if (bossBars.containsKey(player)) return

        val inspectorBossBar = Bukkit.createBossBar("", color, BarStyle.SOLID)
        inspectorBossBar.isVisible = true
        inspectorBossBar.addPlayer(player)
        inspectorBossBars[player] = inspectorBossBar

        if (!playerTimes.containsKey(player)) {
            playerTimes[player] = initialTimeInSeconds
            startTimer(player)
        }
    }

    fun addPlayerToStop(player: Player) {
        val stopBossBar = Bukkit.createBossBar(ReviseConfig.bossBarTextOnStop.convertColor(), color, BarStyle.SOLID)
        stopBossBar.isVisible = true
        stopBossBar.addPlayer(player)
        suspectStopBossBars[player] = stopBossBar
    }

    fun removePlayerFromStop(player: Player) {
        suspectStopBossBars[player]?.removePlayer(player)
    }

    fun removePlayer(player: Player) {
        bossBars[player]?.removePlayer(player)
        inspectorBossBars[player]?.removePlayer(player)
    }

    fun removeInspector(player: Player) {
        inspectorBossBars[player]?.removePlayer(player)
    }

    fun addTime(player: Player, secs: Int): Int {
        val newTime = (playerTimes[player] ?: 0) + secs
        playerTimes[player] = newTime
        return newTime
    }

    fun removeAllPlayers() {
        for (bossBar in bossBars.values) {
            bossBar.removeAll()
        }
        for (inspectorBossBar in inspectorBossBars.values) {
            inspectorBossBar.removeAll()
        }
    }
}

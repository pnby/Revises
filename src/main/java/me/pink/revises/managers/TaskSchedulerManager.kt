package me.pink.revises.managers

import me.pink.revises.api.Scheduler
import me.pink.revises.convertColor
import me.pink.revises.managers.CheckManager.reduceTimeForAllSessions
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.Bukkit

object TaskSchedulerManager {
    private val scheduler: Scheduler =
        Scheduler()

    fun runReduceTimeTask() {
        scheduler.runRepeatingTask({
            reduceTimeForAllSessions(1000L)
        }, 0L, 1000L)
    }

    fun runAFKTrackTask() {
        scheduler.runRepeatingTask({
            AFKTrackManager.startAFKCheckTask()
        }, 0L, 1000L)
    }

    fun runCooldownTimeReduceTask() {
        scheduler.runRepeatingTaskAsync({
            CooldownManager.reduceAllCooldowns()
        }, 0L, 1000L)
    }

    fun runCheckMessageTask() {
        scheduler.runRepeatingTask({
            for (player in Bukkit.getOnlinePlayers()) {
                if (CheckManager.isUnderCheck(player)) {
                    val checkSession = CheckManager.getCheckSession(player)
                    if (checkSession?.suspect == player)
                        ReviseConfig.checkMessage.forEach { message ->
                            player.sendMessage(message.toString().convertColor())
                        }
                }
            }
        }, 0L, ReviseConfig.messageDelay.toLong() * 1000L)
    }
}

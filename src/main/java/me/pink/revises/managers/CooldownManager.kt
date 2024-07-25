package me.pink.revises.managers

import me.pink.revises.utils.CooldownPlayer
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object CooldownManager {
    private val cooldowns: ConcurrentHashMap<Player, MutableList<CooldownPlayer>> = ConcurrentHashMap()

    fun setCooldown(player: Player, cooldownCommand: String, durationSeconds: Int) {
        val cooldownPlayer = CooldownPlayer(player, player.name.lowercase(), cooldownCommand, durationSeconds.toLong())
        cooldowns.getOrPut(player) { mutableListOf() }.add(cooldownPlayer)
    }

    fun clearCooldown(player: Player, cooldownCommand: String) {
        cooldowns[player]?.removeIf { it.cooldownCommand == cooldownCommand }
    }

    fun getRemainingTime(player: Player, cooldownCommand: String): Long {
        return cooldowns[player]?.find { it.cooldownCommand == cooldownCommand }?.remainingTime ?: 0
    }

    fun isOnCooldown(player: Player, cooldownCommand: String): Boolean {
        return getRemainingTime(player, cooldownCommand) > 0
    }

    fun reduceAllCooldowns() {
        cooldowns.forEach { (player, cooldownList) ->
            cooldownList.removeIf { cooldownPlayer ->
                cooldownPlayer.remainingTime -= 1
                cooldownPlayer.remainingTime <= 0
            }
            if (cooldownList.isEmpty()) {
                cooldowns.remove(player)
            }
        }
    }
}
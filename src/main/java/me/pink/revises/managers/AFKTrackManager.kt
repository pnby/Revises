package me.pink.revises.managers

import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*


object AFKTrackManager: Listener {
    private val afkPlayers = mutableSetOf<UUID>()
    private val lastActive = mutableMapOf<UUID, Long>()
    private val afkTimeout = ReviseConfig.considerAFK.toLong() * 1000

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        lastActive[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        afkPlayers.remove(event.player.uniqueId)
        lastActive.remove(event.player.uniqueId)
    }

    @EventHandler
    private fun onPlayerMove(event: PlayerMoveEvent) {
        updatePlayerActivity(event.player.uniqueId)
    }

    @EventHandler
    private fun onPlayerInteract(event: PlayerInteractEvent) {
        updatePlayerActivity(event.player.uniqueId)
    }

    private fun updatePlayerActivity(playerId: UUID) {
        lastActive[playerId] = System.currentTimeMillis()
        afkPlayers.remove(playerId)
    }

    fun inAFKPlayers(uuid: UUID): Boolean {
        return afkPlayers.contains(uuid)
    }

    fun startAFKCheckTask() {
        val currentTime = System.currentTimeMillis()
        for (player in Bukkit.getOnlinePlayers()) {
            val lastActiveTime = lastActive[player.uniqueId]
            if (lastActiveTime != null && currentTime - lastActiveTime >= afkTimeout) {
                afkPlayers.add(player.uniqueId)
            }
        }
    }
}
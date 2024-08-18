package me.pink.revises.listeners.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.config.ReviseConfig

class PlayerInteractListener: Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (CheckManager.isSuspectUnderCheck(event.player)) {
            if (!ReviseConfig.suspectInteraction) {
                val checkSession = CheckManager.getCheckSession(event.player)
                if (event.player == checkSession?.suspect) {
                    event.isCancelled = true
                }
            }
        }
    }
}
package me.pink.revises.listeners.player

import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener: Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (CheckManager.isSuspectUnderCheck(event.player)) {
            if (!ReviseConfig.suspectMove) {
                event.isCancelled = true
            }
        }
    }
}
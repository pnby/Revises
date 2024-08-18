package me.pink.revises.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

class SuspectInteractEvent: Listener {
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

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (CheckManager.isSuspectUnderCheck(event.player)) {
            if (!ReviseConfig.suspectMove) {
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        if (CheckManager.isSuspectUnderCheck(event.entity as Player)) {
            if (!ReviseConfig.suspectDamage) {
                event.isCancelled = true
            }
        }
    }
}
package me.pink.revises.listeners.entity

import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamageListener: Listener {
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
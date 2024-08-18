package me.pink.revises.listeners.entity

import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerDropItemEvent

class EntityInventoryListener: Listener {
    @EventHandler
    fun onPlayerInvInteract(event: InventoryInteractEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        if (CheckManager.isSuspectUnderCheck(player)) {
            if (!ReviseConfig.suspectInvInteraction) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInvClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        val player = event.whoClicked as Player
        if (CheckManager.isSuspectUnderCheck(player)) {
            if (!ReviseConfig.suspectInvClick) {
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onPlayerInvPickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (CheckManager.isSuspectUnderCheck(player)) {
            if (!ReviseConfig.suspectInvPickup) {
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onPlayerInvMoveItem(event: InventoryMoveItemEvent) {
        if (event.initiator.holder !is Player) return
        val player = event.initiator.holder as Player
        if (CheckManager.isSuspectUnderCheck(player)) {
            if (!ReviseConfig.suspectInvMoveItem) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInvDropItem(event: PlayerDropItemEvent) {
        if (CheckManager.isSuspectUnderCheck(event.player)) {
            if (!ReviseConfig.suspectInvDrop) {
                event.isCancelled = true
            }
        }
    }
}
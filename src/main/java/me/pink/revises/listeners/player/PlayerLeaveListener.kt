package me.pink.revises.listeners.player

import me.pink.revises.convertColor
import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class PlayerLeaveListener(private val plugin: Plugin): Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerLeave(event: PlayerQuitEvent) {

        val isUnderChecking = CheckManager.isSuspectUnderCheck(event.player)

        if (isUnderChecking && !plugin.server.isStopping) {
            val checkSession = CheckManager.getCheckSession(event.player) ?: return

            CheckManager.disapproveSuspect(checkSession, ReviseConfig.systemAutoBanCommand.replace("%suspect%", event.player.name), "guilty")
            checkSession.inspector.sendMessage(ReviseConfig.systemExitMessage.convertColor())
            checkSession.inspector.sendMessage(ReviseConfig.systemExitReasonMessage.convertColor().replace("%exit_reason%", event.reason.name))
        }
    }

}
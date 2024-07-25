package me.pink.revises.events.bukkit

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import me.pink.revises.utils.config.ReviseConfig
import me.pink.revises.utils.config.ReviseConfig.chatSideModeratorPlayer
import me.pink.revises.utils.config.ReviseConfig.chatSideModeratorYou
import me.pink.revises.utils.config.ReviseConfig.chatSidePlayerModerator
import me.pink.revises.utils.config.ReviseConfig.chatSidePlayerYou
import me.pink.revises.convertColor
import me.pink.revises.managers.CheckManager
import me.pink.revises.utils.CheckSession

class SuspectChatEvent : Listener, ChatRenderer {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerChatEvent(event: AsyncChatEvent) {
        val player = event.player

        val checkSession = CheckManager.getCheckSession(player)
        if (checkSession != null) {
            if (player == checkSession.suspect || player == checkSession.inspector) {
                event.renderer(this)
                event.isCancelled = true
                val message = event.message()
                val originalMessage = event.originalMessage() as TextComponent

                val moderatorMessage = formatMessage(player, checkSession, message, true)
                val playerMessage = formatMessage(player, checkSession, message, false)

                sendFormattedMessage(checkSession.suspect, playerMessage, originalMessage.content())
                sendFormattedMessage(checkSession.inspector, moderatorMessage, originalMessage.content())

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (CheckManager.isUnderCheck(player) && !ReviseConfig.commandsIsNotToggle) {
            val checkSession = CheckManager.getCheckSession(player)
            if (player == checkSession?.suspect) {
                val command = event.message.split(" ")[0].substring(1)

                if (!ReviseConfig.allowedCommands.contains(command)) {
                    event.isCancelled = true
                    player.sendMessage(ReviseConfig.commandsDenyMessage.convertColor())
                }
            }
        }
    }

    private fun sendFormattedMessage(player: Player, message: List<String>, originalMessage: String) {
        message.forEach { line ->
            var formattedLine = LegacyComponentSerializer.legacyAmpersand().deserialize(line)

            if (ReviseConfig.chatHoverIsToggle) {
                formattedLine = formattedLine
                    .hoverEvent(HoverEvent.showText(Component.text(ReviseConfig.hoverText.convertColor())))
            }
            if (ReviseConfig.chatCopyIsToggle) {
                formattedLine = formattedLine
                    .clickEvent(ClickEvent.copyToClipboard(originalMessage))
            }
            player.sendMessage(formattedLine)
        }
    }

    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val formattedMessage = formatMessage(source, message)

        return formattedMessage
    }

    private fun formatMessage(source: Player, message: Component): Component {
        return Component.text()
            .append(source.displayName())
            .append(Component.text(": "))
            .append(message)
            .build()
    }

    private fun formatMessage(player: Player, checkSession: CheckSession, message: Component, isModerator: Boolean): List<String> {
        val messageText = LegacyComponentSerializer.legacyAmpersand().serialize(message)
        val format = if (isModerator) {
            if (player == checkSession.suspect) chatSideModeratorPlayer else chatSideModeratorYou
        } else {
            if (player == checkSession.suspect) chatSidePlayerYou else chatSidePlayerModerator
        }

        return format.map { replacePlaceholders(it.toString(), messageText, checkSession) }
    }

    private fun replacePlaceholders(line: String, message: String, checkSession: CheckSession): String {
        return line.replace("%suspect%", checkSession.suspect.name)
            .replace("%moderator%", checkSession.inspector.name)
            .replace("%text%", message)
    }

}
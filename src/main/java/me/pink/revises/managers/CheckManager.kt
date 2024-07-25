package me.pink.revises.managers

import me.pink.revises.api.internal.Scheduler
import me.pink.revises.convertColor
import me.pink.revises.database.repositories.CheckRepository
import me.pink.revises.database.repositories.CheckRepository.getCheckById
import me.pink.revises.utils.CheckSession
import me.pink.revises.utils.ItemBuilder
import me.pink.revises.utils.RevisePermissions
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object CheckManager {
    private lateinit var bossBar: BossBarManager
    private val scheduler: Scheduler = Scheduler()
    private val checkSessions = ConcurrentHashMap<Player, CheckSession>()
    private var checkEffects: MutableList<PotionEffectType> = mutableListOf()

    fun startCheck(suspect: Player, inspector: Player, durationMillis: Long, id: UUID) {
        val session = CheckSession(id, suspect, inspector, durationMillis, 0)
        checkSessions[suspect] = session

        teleportPlayerIfNeeded(inspector, suspect)
        applyPlayerEffects(suspect, inspector)
        startMessageSchedule(suspect)
    }

    fun approveSuspect(checkSession: CheckSession) {
        val inspector = checkSession.inspector
        val suspect = checkSession.suspect

        val check = getCheckById(checkSession.uid)

        if (ReviseConfig.loggingToggle) {
            check?.let {
                it.stopCheckTime = LocalDateTime.now()
                it.result = ReviseConfig.loggingResultsVerified
                CheckRepository.updateCheck(it)
            }
        }

        if (ReviseConfig.prizeToggle) {
            giveItem(checkSession.suspect, check?.stopCheckTime.toString())
        }

        cleanUpAfterCheck(suspect, inspector)
        inspector.sendMessage("Игрок был признан невиновным.")
    }

    fun disapproveSuspect(checkSession: CheckSession, punishmentCommand: String, reason: String = "endTime") {
        bossBar.removeInspector(checkSession.inspector)
        bossBar.removePlayerFromStop(checkSession.inspector)
        checkSessions.remove(checkSession.suspect)
        val check = getCheckById(checkSession.uid)

        if (ReviseConfig.loggingToggle) {
            if (reason.equals("endTime", ignoreCase = true)) {
                check?.let {
                    it.stopCheckTime = LocalDateTime.now()
                    it.result = ReviseConfig.loggingResultsEndTime
                    CheckRepository.updateCheck(it)
                }
            } else {
                check?.let {
                    it.stopCheckTime = LocalDateTime.now()
                    it.result = ReviseConfig.loggingResultsBan
                    CheckRepository.updateCheck(it)
                }
            }
        }
        if (ReviseConfig.systemAutoBan) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishmentCommand)
        }
        cleanUpAfterCheck(checkSession.suspect, checkSession.inspector)
    }

    fun getCheckSession(player: Player): CheckSession? {
        return checkSessions.values.find { it.suspect == player || it.inspector == player }
    }

    fun addTime(suspect: Player, inspector: Player, additionalTimeSeconds: Int): Boolean {
        val session = checkSessions[suspect]
        if (session != null) {
            val totalAddedTime = additionalTimeSeconds + session.addedTimeSecs
            if (totalAddedTime <= ReviseConfig.additionalTimeMaximum || inspector.hasPermission(RevisePermissions.BYPASS_ADD_TIME)) {
                val additionalTimeMillis = additionalTimeSeconds * 1000
                session.remainingTimeMillis += additionalTimeMillis
                session.addedTimeSecs += additionalTimeSeconds
                bossBar.addTime(session.suspect, additionalTimeSeconds)
                bossBar.addTime(session.inspector, additionalTimeSeconds)
                return true
            }
        }
        return false
    }

    fun stopTime(session: CheckSession) {
        session.timeStopped = true
        bossBar.removePlayer(session.suspect)
        bossBar.removeInspector(session.inspector)

        if (ReviseConfig.bossBarIsToggle) {
            bossBar.addPlayerToStop(session.suspect)
            bossBar.addPlayerToStop(session.inspector)
        }
    }

    fun isUnderCheck(player: Player): Boolean {
        return checkSessions.values.any { it.suspect == player || it.inspector == player }
    }

    fun isSuspectUnderCheck(player: Player): Boolean {
        return checkSessions.values.any { it.suspect == player }
    }

    fun reduceTimeForAllSessions(elapsedTimeMillis: Long) {
        for (session in checkSessions.values) {
            reduceTime(session.suspect, elapsedTimeMillis)
            if (session.remainingTimeMillis <= 0) {
                if (ReviseConfig.loggingToggle) {
                    val check = getCheckById(session.uid)

                    check?.let {
                        it.stopCheckTime = LocalDateTime.now()
                        it.result = ReviseConfig.loggingResultsEndTime
                        CheckRepository.updateCheck(it)
                    }
                }

                disapproveSuspect(session, ReviseConfig.systemAutoBanCommand.replace("%suspect%", session.suspect.name))
                executeConsoleCommand(ReviseConfig.systemAutoBanCommand.replace("%suspect%", session.suspect.name))
                session.inspector.sendMessage(
                    ReviseConfig.endTimeMessage.convertColor().replace("%suspect%", session.suspect.name)
                )
            }
        }
    }

    fun getAllSuspects(): List<Player> {
        return checkSessions.keys.toList()
    }

    private fun giveItem(suspect: Player, date: String) {
        val material = Material.getMaterial(ReviseConfig.prizeItem)
        val loreList = ReviseConfig.prizeItemLore.map {
            it.toString()
                .replace("%player%", suspect.name)
                .replace("%date%", date)
        }.toMutableList()
        if (material != null) {
            val itemStack = ItemBuilder(material)
                .setName(ReviseConfig.prizeItemName.replace("%player%", suspect.name))
                .setLore(loreList)
                .hideFlags(ReviseConfig.prizeItemHideFlags)
                .build()
            suspect.inventory.addItem(itemStack)
        }
    }

    private fun cleanUpAfterCheck(suspect: Player, inspector: Player) {
        checkSessions.remove(suspect)
        bossBar.removePlayer(suspect)
        bossBar.removePlayerFromStop(suspect)
        bossBar.removeInspector(inspector)
        bossBar.removePlayerFromStop(inspector)
        for (effect in suspect.activePotionEffects) {
            if (effect.type in checkEffects) {
                suspect.removePotionEffect(effect.type)
            }
        }
    }

    private fun reduceTime(suspect: Player, elapsedTimeMillis: Long) {
        val session = checkSessions[suspect]
        if (session != null) {
            if (session.timeStopped) {
                return
            }
            session.remainingTimeMillis -= elapsedTimeMillis
        }
    }

    private fun executeConsoleCommand(command: String) {
        val console: ConsoleCommandSender = Bukkit.getServer().consoleSender
        Bukkit.dispatchCommand(console, command)
    }

    private fun startMessageSchedule(suspect: Player) {
        scheduler.runRepeatingTaskAsync({
            if (isUnderCheck(suspect)) {
                val checkSession = getCheckSession(suspect)
                if (checkSession?.suspect == suspect)
                    ReviseConfig.checkMessage.forEach { message ->
                        suspect.sendMessage(message.toString().convertColor())
                    }
            }
        }, 0L, ReviseConfig.messageDelay.toLong() * 1000L)
    }

    private fun teleportPlayerIfNeeded(inspector: Player, suspect: Player) {
        if (ReviseConfig.teleportInCheckToggle && ReviseConfig.teleportInCheckTpToModer) {
            val moderLocation = inspector.location
            suspect.teleport(moderLocation)
        }

        if (ReviseConfig.teleportInCheckToggle && ReviseConfig.teleportInCheckTpToCoords) {
            val world = Bukkit.getWorld(ReviseConfig.coordsWorldName)
            if (world != null) {
                val customLocation = Location(world, ReviseConfig.coordsX, ReviseConfig.coordsY, ReviseConfig.coordsZ)
                customLocation.pitch = ReviseConfig.pitch.toFloat()
                customLocation.yaw = ReviseConfig.yaw.toFloat()
                suspect.teleport(customLocation)
            } else {
                inspector.sendMessage("Некорректный мир указан в конфигурации.")
            }
        }
    }

    private fun applyPlayerEffects(suspect: Player, inspector: Player) {
        if (ReviseConfig.titlesIsToggle) {
            suspect.sendTitle(ReviseConfig.titlesTitle, ReviseConfig.titlesSubTitle, 10, 20, 10)
        }
        if (ReviseConfig.actionBarIsToggle) {
            suspect.sendActionBar(ReviseConfig.actionBarText.convertColor())
        }
        if (ReviseConfig.bossBarIsToggle) {
            bossBar =
                BossBarManager(BarColor.valueOf(ReviseConfig.bossBarColor), ReviseConfig.defaultCheckTime, scheduler)
            bossBar.addPlayer(suspect)
            bossBar.addInspector(inspector)
        }
        if (ReviseConfig.effectsIsToggle) {
            for (effect in ReviseConfig.effects) {
                suspect.addPotionEffect(PotionEffect(getPotionEffectTypeByName(effect.toString())!!, Int.MAX_VALUE, 10))
                checkEffects.add(PotionEffect(getPotionEffectTypeByName(effect.toString())!!, Int.MAX_VALUE, 10).type)
            }
        }
    }

    private fun getPotionEffectTypeByName(effectName: String): PotionEffectType? {
        return try {
            PotionEffectType.getByName(effectName)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            throw RuntimeException("Unknown potion effect")
        }
    }
}
